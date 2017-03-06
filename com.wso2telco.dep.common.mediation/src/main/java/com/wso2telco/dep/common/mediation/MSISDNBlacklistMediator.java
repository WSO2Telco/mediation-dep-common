package com.wso2telco.dep.common.mediation;

import com.wso2telco.dep.common.mediation.service.APIService;
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
		String apiName = (String) messageContext.getProperty("API_NAME");
		String apiVersion = (String) messageContext.getProperty("VERSION");
		String apiPublisher = (String) messageContext.getProperty("API_PUBLISHER");

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

		try {
			apiID = apiService.getAPIId(apiPublisher, apiName, apiVersion);
			if (apiService.isBlackListedNumber(apiID, formattedPhoneNumber)) {
				log.info(msisdn + " is BlackListed number for " + apiName + " API" + apiVersion + " version");
				messageContext.setProperty(SynapseConstants.ERROR_CODE, "POL0001:");
				messageContext.setProperty(SynapseConstants.ERROR_MESSAGE, "Internal Server Error. Blacklisted " +
						"Number");
				messageContext.setProperty("BLACKLISTED_MSISDN", "true");

				String errorVariable = msisdn;

				if(paramArray != null){
					errorVariable = paramArray;
				}
				setErrorInContext(
						messageContext,
						"SVC0004",
						" blacklisted number. %1",
						errorVariable,
						"400", "POLICY_EXCEPTION");
			} else {
				messageContext.setProperty("BLACKLISTED_MSISDN", "false");
			}
		} catch (Exception e) {

			log.error("error in MSISDNBlacklistMediator mediate : "
					+ e.getMessage());

			String errorVariable = msisdn;

			if(paramArray != null){
				errorVariable = paramArray;
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
