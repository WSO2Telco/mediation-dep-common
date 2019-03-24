package com.wso2telco.dep.common.mediation;

import com.wso2telco.dep.common.mediation.service.APIService;
import com.wso2telco.dep.common.mediation.util.MSISDNConstants;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.mediators.AbstractMediator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MSISDNBlacklistMediator extends AbstractMediator {

	private void setErrorInContext(MessageContext synContext, String messageId,
	                               String errorText, String errorVariable, String httpStatusCode,
	                               String exceptionType) {

		synContext.setProperty("messageId", messageId);
		synContext.setProperty("mediationErrorText", errorText);
		synContext.setProperty("errorVariable", errorVariable);
		synContext.setProperty("httpStatusCode", httpStatusCode);
		synContext.setProperty("exceptionType", exceptionType);
	}

	public boolean mediate(MessageContext messageContext) {

		String msisdn = (String) messageContext.getProperty("paramValue");
		String paramArray = (String) messageContext.getProperty("paramArray");
		String maskedMsidsn = (String) messageContext.getProperty("MASKED_MSISDN");
		String maskedMsisdnSuffix = (String) messageContext.getProperty("MASKED_MSISDN_SUFFIX");
		String maskedMsidsnArray = (String) messageContext.getProperty("MASKED_MSISDN_LIST");
		String apiName = (String) messageContext.getProperty("API_NAME");
		String apiVersion = (String) messageContext.getProperty("VERSION");
		String apiPublisher = (String) messageContext.getProperty("API_PUBLISHER");
		String secretKey = (String)messageContext.getProperty(MSISDNConstants.USER_MASKING_SECRET_KEY);

		String loggingMsisdn = msisdn;

		String apiID;
		APIService apiService = new APIService();

		String regexPattern = (String) messageContext.getProperty("msisdnRegex");
		String regexGroupNumber = (String) messageContext.getProperty("msisdnRegexGroup");

		Pattern pattern = Pattern.compile(regexPattern);
		Matcher matcher = pattern.matcher(msisdn);

		String formattedPhoneNumber = null;
		if (matcher.matches()) {
			formattedPhoneNumber = matcher.group(Integer.parseInt(regexGroupNumber));
		}

		if(Boolean.parseBoolean((String)messageContext.getProperty("USER_ANONYMIZATION"))) {
			loggingMsisdn = maskedMsidsn;
			formattedPhoneNumber = maskedMsisdnSuffix;
		}

		try {
			apiID = apiService.getAPIId(apiPublisher, apiName, apiVersion);
			if (apiService.isBlackListedNumber(apiID, formattedPhoneNumber, secretKey)) {
				log.info(loggingMsisdn + " is BlackListed number for " + apiName + " API" + apiVersion + " version");
				messageContext.setProperty(SynapseConstants.ERROR_CODE, "POL0001:");
				messageContext.setProperty(SynapseConstants.ERROR_MESSAGE, "Internal Server Error. Blacklisted " +
						"Number");
				messageContext.setProperty("BLACKLISTED_MSISDN", "true");

				setErrorInContext(
						messageContext,
						"SVC0004",
						" blacklisted number. %1",
						 msisdn,
						"400", "POLICY_EXCEPTION");
			} else {
				messageContext.setProperty("BLACKLISTED_MSISDN", "false");
			}
		} catch (Exception e) {

			log.error("error in MSISDNBlacklistMediator mediate : "
					+ e.getMessage());

			String errorVariable = loggingMsisdn;

			if(paramArray != null){
				errorVariable = paramArray;
				if(Boolean.valueOf((String)messageContext.getProperty("USER_ANONYMIZATION")).booleanValue()) {
					errorVariable = maskedMsidsnArray;
				}
			}
			setErrorInContext(
					messageContext,
					"SVC0001",
					"A service error occurred. Error code is %1",
					errorVariable,
					"500", "SERVICE_EXCEPTION");
			messageContext.setProperty("INTERNAL_ERROR", "true");
		}
		return true;
	}
}
