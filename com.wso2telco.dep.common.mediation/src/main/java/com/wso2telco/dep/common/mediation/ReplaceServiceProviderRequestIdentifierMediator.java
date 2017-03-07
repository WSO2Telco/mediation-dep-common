package com.wso2telco.dep.common.mediation;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

public class ReplaceServiceProviderRequestIdentifierMediator extends AbstractMediator {

	private void setErrorInContext(MessageContext synContext, String messageId,
			String errorText, String errorVariable, String httpStatusCode,
			String exceptionType) {

		synContext.setProperty("messageId", messageId);
		synContext.setProperty("errorText", errorText);
		synContext.setProperty("errorVariable", errorVariable);
		synContext.setProperty("httpStatusCode", httpStatusCode);
		synContext.setProperty("exceptionType", exceptionType);
	}

	public boolean mediate(MessageContext synContext) {

		try {

			String resourcePath = (String) synContext.getProperty("RESOURCE");
			String uniqueRequestIdentifier = (String) synContext.getProperty("uniqueRequestIdentifier");
			String changedResourcePath = resourcePath.replaceAll("(?<=[?&;])requestIdentifier=[^&;]*",
					"requestIdentifier=" + uniqueRequestIdentifier);
			synContext.setProperty("API_ENDPOINT", changedResourcePath);
		} catch (Exception e) {

			log.error("error in ReplaceRequestIdetifierMediator mediate : "
					+ e.getMessage());
			setErrorInContext(
					synContext,
					"SVC0001",
					"A service error occurred. Error code is %1",
					"An internal service error has occured. Please try again later.",
					"500", "SERVICE_EXCEPTION");
			synContext.setProperty("INTERNAL_ERROR", "true");
		}
		
		return true;
	}
}
