package com.wso2telco.dep.common.mediation;

import com.wso2telco.dep.common.mediation.service.APIService;
import com.wso2telco.dep.common.mediation.util.ContextPropertyName;
import com.wso2telco.dep.common.mediation.util.ExceptionType;
import com.wso2telco.dep.common.mediation.util.ErrorConstants;
import org.apache.http.HttpStatus;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.mediators.AbstractMediator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MSISDNBlacklistMediator extends AbstractMediator {

	private void setErrorInContext(MessageContext synContext, String messageId,
	                               String errorText, String errorVariable, String httpStatusCode,
	                               String exceptionType) {

		synContext.setProperty(ContextPropertyName.MESSAGE_ID, messageId);
		synContext.setProperty("mediationErrorText", errorText);    // mediationErrorText  ContextPropertyName.ERROR_TEXT
		synContext.setProperty(ContextPropertyName.ERROR_VARIABLE, errorVariable);
		synContext.setProperty(ContextPropertyName.HTTP_STATUS_CODE, httpStatusCode);
		synContext.setProperty(ContextPropertyName.EXCEPTION_TYPE, exceptionType);
	}

	public boolean mediate(MessageContext messageContext) {

		String msisdn = (String) messageContext.getProperty("paramValue");
		String paramArray = (String) messageContext.getProperty("paramArray");
		String maskedMsidsn = (String) messageContext.getProperty("MASKED_MSISDN");
		String maskedMsisdnSuffix = (String) messageContext.getProperty("MASKED_MSISDN_SUFFIX");
		String apiName = (String) messageContext.getProperty("API_NAME");
		String apiVersion = (String) messageContext.getProperty("VERSION");
		String apiPublisher = (String) messageContext.getProperty("API_PUBLISHER");

		String loggingMsisdn = msisdn;

		String apiID;
		APIService apiService = new APIService();

		String regexPattern = (String) messageContext.getProperty("msisdnRegex");
		String regexGroupNumber = (String) messageContext.getProperty("msisdnRegexGroup");

		Pattern pattern = Pattern.compile(regexPattern);
		Matcher matcher = pattern.matcher(msisdn);

		String formattedPhoneNumber = null;
		if(Boolean.parseBoolean((String)messageContext.getProperty("USER_ANONYMIZATION"))) {
			loggingMsisdn = maskedMsidsn;
			formattedPhoneNumber = maskedMsisdnSuffix;
		} else if (matcher.matches()) {
			formattedPhoneNumber = matcher.group(Integer.parseInt(regexGroupNumber));
		}

		try {
			apiID = apiService.getAPIId(apiPublisher, apiName, apiVersion);
			if (apiService.isBlackListedNumber(apiID, formattedPhoneNumber)) {
				log.info(loggingMsisdn + " is BlackListed number for " + apiName + " API" + apiVersion + " version");

				messageContext.setProperty(SynapseConstants.ERROR_CODE, "POL0001:");
				messageContext.setProperty(SynapseConstants.ERROR_MESSAGE, "Internal Server Error. Blacklisted " +
						"Number");
				messageContext.setProperty("BLACKLISTED_MSISDN", "true");

				setErrorInContext(
						messageContext,
						ErrorConstants.POL0001,
						ErrorConstants.POL0001_TEXT,
						"Blacklisted Number: " + msisdn,
						Integer.toString(HttpStatus.SC_BAD_REQUEST), ExceptionType.POLICY_EXCEPTION.toString());
			} else {
				messageContext.setProperty("BLACKLISTED_MSISDN", "false");
			}
		} catch (Exception e) {
			log.error("error in MSISDNBlacklistMediator mediate : " + e.getMessage());

			String errorVariable = msisdn;
			if(paramArray != null){
				errorVariable = paramArray;
			}

			setErrorInContext(
					messageContext,
					ErrorConstants.SVC0001,
					ErrorConstants.SVC0001_TEXT,
					errorVariable,
					Integer.toString(HttpStatus.SC_INTERNAL_SERVER_ERROR), ExceptionType.SERVICE_EXCEPTION.toString());
			messageContext.setProperty(ContextPropertyName.INTERNAL_ERROR, "true");
		}
		return true;
	}
}
