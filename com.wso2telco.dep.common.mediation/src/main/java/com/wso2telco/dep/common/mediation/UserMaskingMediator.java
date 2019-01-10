package com.wso2telco.dep.common.mediation;

import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Map;

public class UserMaskingMediator extends AbstractMediator {

    public boolean mediate(MessageContext messageContext) {
        try {
            // Getting the json payload to string
            String jsonString = JsonUtil.jsonPayloadToString(((Axis2MessageContext) messageContext).getAxis2MessageContext());
            JSONObject jsonBody = new JSONObject(jsonString);
            // Getting API handler
            String handler = (String) messageContext.getProperty("handler");
            if (handler != null) {
                if (handler.equals("SendSMSHandler")) {
                    if (!jsonBody.isNull("outboundSMSMessageRequest")) {
                        JSONObject outboundSMSMessageRequest = jsonBody.getJSONObject("outboundSMSMessageRequest");

                        if (!outboundSMSMessageRequest.isNull("address")) {
                            String[] addressList = null;
                            if (Boolean.valueOf((String)messageContext.getProperty("anonymize"))) {
                                addressList = ((String)messageContext.getProperty("MASKED_MSISDN_LIST")).split(",");
                            } else {
                                addressList = ((String)messageContext.getProperty("MSISDN_LIST")).split(",");
                            }
                            JSONArray addresses = new JSONArray();

                            for (int i = 0; i < addressList.length; i++) {
                                addresses.put(addressList[i]);
                            }
                            outboundSMSMessageRequest.put("address", addresses);
                        }
                        if (!outboundSMSMessageRequest.isNull("deliveryInfoList")) {
                            JSONObject deliveryInfoList = outboundSMSMessageRequest.getJSONObject("deliveryInfoList");
                            if (!deliveryInfoList.isNull("deliveryInfo")) {
                                Map<String, String> maskedMsisdnMap = (Map) messageContext.getProperty("MASKED_MSISDN_MAP");
                                JSONArray deliveryInfoArray = deliveryInfoList.getJSONArray("deliveryInfo");
                                JSONArray newDeliveryInfoArray = new JSONArray();
                                for (int i = 0; i < deliveryInfoArray.length(); i++) {
                                    JSONObject deliveryInfo = (JSONObject) deliveryInfoArray.get(i);
                                    JSONObject newDeliveryInfo = new JSONObject();
                                    newDeliveryInfo.put("deliveryStatus", (String)deliveryInfo.get("deliveryStatus"));
                                    if (Boolean.valueOf((String)messageContext.getProperty("anonymize"))) {
                                        // Replace with masked user ID
                                        newDeliveryInfo.put("address", getKeyFromValue(maskedMsisdnMap, (String) deliveryInfo.get("address")));
                                    } else {
                                        // Replace with user ID
                                        newDeliveryInfo.put("address", maskedMsisdnMap.get((String) deliveryInfo.get("address")));
                                    }
                                    newDeliveryInfoArray.put(i, newDeliveryInfo);
                                }
                                deliveryInfoList.put("deliveryInfo", newDeliveryInfoArray);
                            }
                            outboundSMSMessageRequest.put("deliveryInfoList", deliveryInfoList);
                        }

                        jsonBody.put("outboundSMSMessageRequest", outboundSMSMessageRequest);
                    }

                } else if (handler.equals("AmountChargeHandler")) {
                    if (!jsonBody.isNull("amountTransaction")) {
                        JSONObject amountTransaction = jsonBody.getJSONObject("amountTransaction");
                        Object headers = ((Axis2MessageContext) messageContext).getAxis2MessageContext()
                                .getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);

                        Map headersMap = null;
                        if (headers != null && headers instanceof Map) {
                            headersMap = (Map) headers;
                        }

                        String userId = null;
                        if (Boolean.valueOf((String)messageContext.getProperty("anonymize"))) {
                            userId = (String) messageContext.getProperty("MASKED_MSISDN");
                            headersMap.put("RESOURCE", (String)messageContext.getProperty("MASKED_RESOURCE"));
                        } else {
                            userId = (String) messageContext.getProperty("MSISDN");
                            headersMap.put("RESOURCE", "/" + URLEncoder.encode(userId, "UTF-8") + "/transactions/amount");
                        }
                        amountTransaction.put("endUserId", userId);
                        jsonBody.put("amountTransaction", amountTransaction);
                    }
                }

                JsonUtil.newJsonPayload(((Axis2MessageContext) messageContext).getAxis2MessageContext(), jsonBody.toString(),
                        true, true);
            }
        } catch (Exception e) {

            log.error("error in UserMaskingMediator mediate : "
                    + e.getMessage());
            setErrorInContext(
                    messageContext,
                    "SVC0001",
                    "A service error occurred. Error code is %1",
                    null, "500", "SERVICE_EXCEPTION");
            messageContext.setProperty("INTERNAL_ERROR", "true");
        }
        return true;
    }

    private void setErrorInContext(MessageContext synContext, String messageId,
                                   String errorText, String errorVariable, String httpStatusCode,
                                   String exceptionType) {

        synContext.setProperty("messageId", messageId);
        synContext.setProperty("mediationErrorText", errorText);
        synContext.setProperty("errorVariable", errorVariable);
        synContext.setProperty("httpStatusCode", httpStatusCode);
        synContext.setProperty("exceptionType", exceptionType);
    }

    public static Object getKeyFromValue(Map maskedMsisdnMap, Object value) {
        for (Object o : maskedMsisdnMap.keySet()) {
            if (maskedMsisdnMap.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }
}
