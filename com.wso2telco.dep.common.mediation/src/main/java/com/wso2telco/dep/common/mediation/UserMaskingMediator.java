/*******************************************************************************
 * Copyright  (c) 2015-2019, WSO2.Telco Inc. (http://www.wso2telco.com) All Rights Reserved.
 *
 * WSO2.Telco Inc. licences this file to you under  the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.wso2telco.dep.common.mediation;

import com.wso2telco.dep.common.mediation.util.*;
import org.apache.http.HttpStatus;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class UserMaskingMediator extends AbstractMediator {

    public boolean mediate(MessageContext messageContext) {
        try {
            // Getting the json payload to string
            String jsonString = JsonUtil.jsonPayloadToString(((Axis2MessageContext) messageContext).getAxis2MessageContext());
            JSONObject jsonBody = new JSONObject(jsonString);
            // Getting API handler
            String handler = (String) messageContext.getProperty("handler");
            Object headers = ((Axis2MessageContext) messageContext).getAxis2MessageContext().
                    getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
            Map headersMap = null;
            if (headers instanceof Map) {
                headersMap = (Map) headers;
            }

            if (handler != null) {
                if (handler.equals(Handler.SendSMSHandler.toString())) {

                    if (!jsonBody.isNull(MSISDNConstants.OUTBOUND_SMS_MESSAGE_REQUEST)) {
                        JSONObject outboundSMSMessageRequest = jsonBody.getJSONObject(MSISDNConstants.OUTBOUND_SMS_MESSAGE_REQUEST);
                        if (!outboundSMSMessageRequest.isNull(MSISDNConstants.ADDRESS)) {
                            String[] addressList = null;
                            if (Boolean.valueOf((String)messageContext.getProperty(MSISDNConstants.ANONYMIZE))) {
                                addressList = ((String)messageContext.getProperty("MASKED_MSISDN_LIST")).split(",");
                                JSONObject deliveryInfoList = outboundSMSMessageRequest.getJSONObject(MSISDNConstants.DELIVERY_INFO_LIST);
                                if ((!deliveryInfoList.isNull(AttributeName.DELIVERY_INFO)) && (!validateSMSOperatorResponse(messageContext,
                                            (new ArrayList<String>(Arrays.asList(((String)messageContext.
                                                    getProperty("MSISDN_SUFFIX_LIST")).split(",")))), outboundSMSMessageRequest))) {
                                        return true;

                                }
                                headersMap.put(AttributeName.RESOURCE, (String)messageContext.getProperty("SMS_RESOURCE"));

                            } else {
                                addressList = ((String)messageContext.getProperty("MSISDN_LIST")).split(",");
                                messageContext.setProperty("SMS_RESOURCE", (String)headersMap.get(AttributeName.RESOURCE));
                            }
                            JSONArray addresses = new JSONArray();

                            for (String address : addressList) {
                                addresses.put(address);
                            }
                            outboundSMSMessageRequest.put(MSISDNConstants.ADDRESS, addresses);
                        }
                        if (!outboundSMSMessageRequest.isNull(MSISDNConstants.DELIVERY_INFO_LIST)) {
                            JSONObject deliveryInfoList = outboundSMSMessageRequest.getJSONObject(MSISDNConstants.DELIVERY_INFO_LIST);
                            if (!deliveryInfoList.isNull(AttributeName.DELIVERY_INFO)) {
                                Map<String, String> maskedMsisdnMap = (Map) messageContext.getProperty("MASKED_MSISDN_SUFFIX_MAP");
                                JSONArray deliveryInfoArray = deliveryInfoList.getJSONArray(AttributeName.DELIVERY_INFO);
                                JSONArray newDeliveryInfoArray = new JSONArray();
                                for (int i = 0; i < deliveryInfoArray.length(); i++) {
                                    JSONObject deliveryInfo = (JSONObject) deliveryInfoArray.get(i);
                                    JSONObject newDeliveryInfo = new JSONObject();
                                    newDeliveryInfo.put("deliveryStatus", (String)deliveryInfo.get("deliveryStatus"));
                                    if (Boolean.valueOf((String)messageContext.getProperty(MSISDNConstants.ANONYMIZE))) {
                                        // Replace with masked user ID
                                        newDeliveryInfo.put(MSISDNConstants.ADDRESS, getKeyFromValue(
                                                maskedMsisdnMap, (String) deliveryInfo.get(MSISDNConstants.ADDRESS)));
                                    } else {
                                        // Replace with user ID
                                        newDeliveryInfo.put(MSISDNConstants.ADDRESS, maskedMsisdnMap.get((String) deliveryInfo.get(MSISDNConstants.ADDRESS)));
                                    }
                                    newDeliveryInfoArray.put(i, newDeliveryInfo);
                                }
                                deliveryInfoList.put(AttributeName.DELIVERY_INFO, newDeliveryInfoArray);
                            }
                            outboundSMSMessageRequest.put(MSISDNConstants.DELIVERY_INFO_LIST, deliveryInfoList);
                        }

                        jsonBody.put(MSISDNConstants.OUTBOUND_SMS_MESSAGE_REQUEST, outboundSMSMessageRequest);
                    }

                } else if ((handler.equals(Handler.AmountChargeHandler.toString()) || handler.equals(Handler.AmountRefundHandler.toString())) &&
                        (!jsonBody.isNull(AttributeName.AMOUNT_TRANSACTION))) {
                        JSONObject amountTransaction = jsonBody.getJSONObject(AttributeName.AMOUNT_TRANSACTION);

                        String userId = null;
                        String maskedMSISDNSuffix = (String)messageContext.getProperty("MASKED_MSISDN_SUFFIX");
                        String msisdnSuffix = (String)messageContext.getProperty("UserMSISDN");
                        String payloadMSISDN =  amountTransaction.getString("endUserId");

                        if (Boolean.valueOf((String)messageContext.getProperty(MSISDNConstants.ANONYMIZE))) {
                            if (!msisdnSuffix.equals(MSISDNUtils.getMSISDNSuffix(payloadMSISDN))) {
                                log.error("Operator returned incorrect msisdn.");
                                setErrorInContext(
                                        messageContext,
                                        ErrorConstants.SVC0001,
                                        ErrorConstants.SVC0001_TEXT,
                                        "operator_msisdn_mismatched", Integer.toString(HttpStatus.SC_INTERNAL_SERVER_ERROR), ExceptionType.SERVICE_EXCEPTION.toString());
                                messageContext.setProperty("INTERNAL_ERROR", "true");
                                return true;
                            }
                            userId = (String) messageContext.getProperty("MASKED_MSISDN");
                            headersMap.put(AttributeName.RESOURCE, (String)messageContext.getProperty("MASKED_RESOURCE"));
                            String resourceURL = (String) amountTransaction.get("resourceURL");
                            resourceURL = resourceURL.replace(msisdnSuffix, maskedMSISDNSuffix);
                            amountTransaction.put("resourceURL", resourceURL);
                        } else {
                            userId = (String) messageContext.getProperty("MSISDN");
                            headersMap.put(AttributeName.RESOURCE, "/" + URLEncoder.encode(userId, "UTF-8") + "/transactions/amount");
                        }
                        amountTransaction.put("endUserId", userId);
                        jsonBody.put(AttributeName.AMOUNT_TRANSACTION, amountTransaction);
                }

                JsonUtil.getNewJsonPayload(((Axis2MessageContext) messageContext).getAxis2MessageContext(), jsonBody.toString(),
                        true, true);
            }
        } catch (Exception e) {

            log.error("error in UserMaskingMediator mediate : " + e.getMessage());
            setErrorInContext(
                    messageContext,
                    ErrorConstants.SVC0001,
                    ErrorConstants.SVC0001_TEXT,
                    null, Integer.toString(HttpStatus.SC_INTERNAL_SERVER_ERROR), ExceptionType.SERVICE_EXCEPTION.toString());
            messageContext.setProperty(ContextPropertyName.INTERNAL_ERROR, "true");
        }
        return true;
    }

    private void setErrorInContext(MessageContext synContext, String messageId,
                                   String errorText, String errorVariable, String httpStatusCode,
                                   String exceptionType) {

        synContext.setProperty(ContextPropertyName.MESSAGE_ID, messageId);
        synContext.setProperty(ContextPropertyName.ERROR_TEXT, errorText);
        synContext.setProperty(ContextPropertyName.ERROR_VARIABLE, errorVariable);
        synContext.setProperty(ContextPropertyName.HTTP_STATUS_CODE, httpStatusCode);
        synContext.setProperty(ContextPropertyName.EXCEPTION_TYPE, exceptionType);
    }

    public static Object getKeyFromValue(Map maskedMsisdnMap, String value) {
        for (Object o : maskedMsisdnMap.keySet()) {
            if (value!= null && value.contains((String)maskedMsisdnMap.get(o))) {
                return value.replace((String)maskedMsisdnMap.get(o), (String)o);
            }
        }
        return null;
    }

    private boolean validateSMSOperatorResponse(MessageContext messageContext, ArrayList<String> requestAddressesSuffixes, JSONObject payload) {
        if (!validateOperatorAddressesWithRequestAddresses(requestAddressesSuffixes, payload)) {
            log.error("Operator returned incorrect Addresses.");
            setErrorInContext(
                    messageContext,
                    ErrorConstants.SVC0001,
                    ErrorConstants.SVC0001_TEXT,
                    "operator_addresses_mismatched", Integer.toString(HttpStatus.SC_INTERNAL_SERVER_ERROR), ExceptionType.SERVICE_EXCEPTION.toString());
            messageContext.setProperty(ContextPropertyName.INTERNAL_ERROR, "true");
            return false;
        }
        if (!validateOperatorDeliveryInfosWithRequestAddresses(requestAddressesSuffixes, payload)) {
            log.error("Operator returned incorrect deliveryInfos.");
            setErrorInContext(
                    messageContext,
                    ErrorConstants.SVC0001,
                    ErrorConstants.SVC0001_TEXT,
                    "operator_deliveryinfo_mismatched", Integer.toString(HttpStatus.SC_INTERNAL_SERVER_ERROR), ExceptionType.SERVICE_EXCEPTION.toString());
            messageContext.setProperty(ContextPropertyName.INTERNAL_ERROR, "true");
            return false;
        }
        return true;
    }

    private boolean validateOperatorAddressesWithRequestAddresses(ArrayList<String> requestAddressesSuffixes, JSONObject payload) {
        JSONArray addresses = payload.getJSONArray(MSISDNConstants.ADDRESS);
        ArrayList<String> payloadAddressSuffixes = new ArrayList<>();
        if (addresses != null) {
            for (int i = 0; i < addresses.length(); i++) {
                payloadAddressSuffixes.add(MSISDNUtils.getMSISDNSuffix(addresses.get(i).toString()));
            }
        }
        return requestAddressesSuffixes.containsAll(payloadAddressSuffixes) && payloadAddressSuffixes.containsAll(requestAddressesSuffixes);
    }

    private boolean validateOperatorDeliveryInfosWithRequestAddresses(ArrayList<String> requestAddressesSuffixes, JSONObject payload) {
        if (!payload.isNull(MSISDNConstants.DELIVERY_INFO_LIST)) {
            JSONObject deliveryInfoList = payload.getJSONObject(MSISDNConstants.DELIVERY_INFO_LIST);
            if (!deliveryInfoList.isNull(AttributeName.DELIVERY_INFO)) {
                JSONArray deliveryInfoArray = deliveryInfoList.getJSONArray(AttributeName.DELIVERY_INFO);
                ArrayList<String> payloadAddressSuffixes = new ArrayList<>();
                if (deliveryInfoArray.length() > 0) {
                    for (int i = 0; i < deliveryInfoArray.length(); i++) {
                        payloadAddressSuffixes.add(MSISDNUtils.getMSISDNSuffix(deliveryInfoArray.getJSONObject(i).getString(
                                MSISDNConstants.ADDRESS)));
                    }
                }else {
                    return true;
                }
                return requestAddressesSuffixes.containsAll(payloadAddressSuffixes) && payloadAddressSuffixes.containsAll(requestAddressesSuffixes);
            }
        }
        return true;
    }
}
