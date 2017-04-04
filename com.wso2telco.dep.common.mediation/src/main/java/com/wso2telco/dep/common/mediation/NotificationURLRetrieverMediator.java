package com.wso2telco.dep.common.mediation;

import java.util.Map;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import com.wso2telco.dep.common.mediation.service.APIService;

public class NotificationURLRetrieverMediator extends AbstractMediator {

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

			String notifyurldid = (String) synContext
					.getProperty("NOTIFY_URL_ID");

			APIService apiService = new APIService();
			Map<String, String> notificationURLInformation = apiService
					.getNotificationURLInformation(Integer
							.parseInt(notifyurldid));

			if (!notificationURLInformation.isEmpty()) {

				synContext.setProperty("NOTIFY_INFO_API_NAME",
						notificationURLInformation.get("apiname"));
				synContext.setProperty("NOTIFY_INFO_URL",
						notificationURLInformation.get("notifyurl"));
				synContext.setProperty("NOTIFY_INFO_SERVICE_PROVIDER",
						notificationURLInformation.get("serviceprovider"));
				synContext.setProperty("NOTIFY_INFO_STATUS",
						notificationURLInformation.get("notifystatus"));
				synContext.setProperty("NOTIFY_INFO_CLIENT_CORRELATOR",
						notificationURLInformation.get("clientCorrelator"));
				synContext.setProperty("NOTIFY_INFO_OPERATOR_NAME",
						notificationURLInformation.get("operatorName"));
				synContext.setProperty("EMPTY_NOTIFY_URL_INFO", "false");
			} else {

				log.error("notify url information unavalible for notify url id : "
						+ notifyurldid);
				setErrorInContext(
						synContext,
						"SVC0001",
						"A service error occurred. Error code is %1",
						"An internal service error has occured. Please try again later.",
						"500", "SERVICE_EXCEPTION");
				synContext.setProperty("EMPTY_NOTIFY_URL_INFO", "true");
			}
		} catch (Exception e) {

			log.error("error in NotificationURLRetrieverMediator mediate : "
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
