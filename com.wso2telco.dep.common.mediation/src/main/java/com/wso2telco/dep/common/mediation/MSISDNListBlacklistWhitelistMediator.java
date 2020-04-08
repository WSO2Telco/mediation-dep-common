/*******************************************************************************
 * Copyright  (c) 2015-2016, WSO2.Telco Inc. (http://www.wso2telco.com) All Rights Reserved.
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

import com.wso2telco.dep.common.mediation.constant.MSISDNConstants;
import com.wso2telco.dep.common.mediation.service.APIService;
import com.wso2telco.dep.common.mediation.util.ContextPropertyName;
import com.wso2telco.dep.common.mediation.util.ErrorConstants;
import com.wso2telco.dep.common.mediation.util.ExceptionType;
import org.apache.http.HttpStatus;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.mediators.AbstractMediator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Common mediator class for both blacklist and whitelist features
 */
public class MSISDNListBlacklistWhitelistMediator extends AbstractMediator {

    private void setErrorInContext(MessageContext synContext, String messageId,
                                   String errorText, String errorVariable, String httpStatusCode,
                                   String exceptionType) {

        synContext.setProperty(ContextPropertyName.MESSAGE_ID, messageId);
        synContext.setProperty("mediationErrorText", errorText);    // mediationErrorText  ContextPropertyName
        // .ERROR_TEXT
        synContext.setProperty(ContextPropertyName.ERROR_VARIABLE, errorVariable);
        synContext.setProperty(ContextPropertyName.HTTP_STATUS_CODE, httpStatusCode);
        synContext.setProperty(ContextPropertyName.EXCEPTION_TYPE, exceptionType);
    }

    public boolean mediate(MessageContext messageContext) {

        String msisdnList = (String) messageContext.getProperty("paramValueList");
        String[] addressList = null;
        if (msisdnList != null && !msisdnList.equals("")) {
            addressList = msisdnList.split(",");
            try {
	            String maskedMsidsn = (String) messageContext.getProperty("MASKED_MSISDN");
	            String maskedMsisdnSuffix = (String) messageContext.getProperty("MASKED_MSISDN_SUFFIX");
	            String apiName = (String) messageContext.getProperty("API_NAME");
	            String apiVersion = (String) messageContext.getProperty("VERSION");
	            String spName = ((String) messageContext.getProperty("USER_ID")).split("@")[0];
	            String appID = messageContext.getProperty("api.ut.application.id").toString();
	            String apiID = (String) messageContext.getProperty("API_ID");
	            String action = (String) messageContext.getProperty("ACTION");
	            
	            boolean hasMatchingNumber = false;
	            String msisdn = null;
	            String errorMsisdnList = "";
	            String checkedMsisdnList = "";
	            
	            for (String address : addressList) {
	                msisdn = address;
	
	                String loggingMsisdn = msisdn;
	
	                String regexPattern = (String) messageContext.getProperty("msisdnRegex");
	                String regexGroupNumber = (String) messageContext.getProperty("msisdnRegexGroup");
	
	                Pattern pattern = Pattern.compile(regexPattern);
	                Matcher matcher = pattern.matcher(msisdn);
	
	                String formattedPhoneNumber = null;
	                if (Boolean.parseBoolean((String) messageContext.getProperty("USER_ANONYMIZATION"))) {
	                    loggingMsisdn = maskedMsidsn;
	                    formattedPhoneNumber = maskedMsisdnSuffix;
	                } else if (matcher.matches()) {
	                    formattedPhoneNumber = matcher.group(Integer.parseInt(regexGroupNumber));
	                }
	
	                APIService apiService = new APIService();
	
	                    if (apiService.isBlackListedorWhiteListedNumber(formattedPhoneNumber, apiID, appID, spName, action)) {
	                        log.info(loggingMsisdn + " is action:" + action + " number for " + apiName + " API " + apiVersion +
	                                " version");
	                        hasMatchingNumber = true;
	                        if (!errorMsisdnList.equals("")) {
	                        	errorMsisdnList += ",";
	                        }
	                    	errorMsisdnList += formattedPhoneNumber;
	                    } else {
	                        if (!checkedMsisdnList.equals("")) {
	                        	checkedMsisdnList += ",";
	                        }
	                        checkedMsisdnList += formattedPhoneNumber;
	                    }
	            }
	            if (hasMatchingNumber) {
	            	if (action.equalsIgnoreCase(MSISDNConstants.BLACKLIST)) {
	                    this.setErrorResponseMessageContext(messageContext, action, MSISDNConstants.BLACKLIST, true);
	
	                    setErrorInContext(
	                            messageContext,
	                            ErrorConstants.POL0001,
	                            ErrorConstants.POL0001_TEXT,
	                            "Blacklisted Numbers: " + errorMsisdnList,
	                            Integer.toString(HttpStatus.SC_BAD_REQUEST), ExceptionType.POLICY_EXCEPTION.toString());
	                } else {
	                    messageContext.setProperty(MSISDNConstants.WHITELIST, "true");
	                }
	            } else {
	            	if (action.equalsIgnoreCase(MSISDNConstants.WHITELIST)) {
	                    this.setErrorResponseMessageContext(messageContext, action, MSISDNConstants.WHITELIST, false);
	
	                    setErrorInContext(
	                            messageContext,
	                            ErrorConstants.POL0001,
	                            ErrorConstants.POL0001_TEXT,
	                            "Not Whitelisted Numbers: " + checkedMsisdnList,
	                            Integer.toString(HttpStatus.SC_BAD_REQUEST), ExceptionType.POLICY_EXCEPTION.toString());
	                } else {
	                    messageContext.setProperty(MSISDNConstants.BLACKLIST, "false");
	                }
	            }
            } catch (Exception e) {
                log.error("error in MSISDNBlacklistWhitelistMediator mediate : " + e.getMessage());

                String errorVariable = msisdnList;
                String paramArray = (String) messageContext.getProperty("paramArray");
                if (paramArray != null) {
                    errorVariable = paramArray;
                }

                setErrorInContext(
                        messageContext,
                        ErrorConstants.SVC0001,
                        ErrorConstants.SVC0001_TEXT,
                        errorVariable,
                        Integer.toString(HttpStatus.SC_INTERNAL_SERVER_ERROR), ExceptionType.SERVICE_EXCEPTION.toString());
                messageContext.setProperty(ContextPropertyName.INTERNAL_ERROR, "true");

                return false;
            }
        } else {
        	//paramValueList null
        }

        return true;
    }

    private MessageContext setErrorResponseMessageContext(MessageContext messageContext,
                                                          String action,
                                                          String type,
                                                          boolean result) {
        messageContext.setProperty(SynapseConstants.ERROR_CODE, "POL0001:");
        messageContext.setProperty(SynapseConstants.ERROR_MESSAGE,
                "Internal Server Error. action: " + action + "Number");
        messageContext.setProperty(type, result);

        return messageContext;
    }
}
