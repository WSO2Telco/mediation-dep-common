package com.wso2telco.dep.common.mediation;

import java.util.Date;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import com.wso2telco.dep.operatorservice.service.OparatorService;

public class DefaultTokenUpdaterMediator extends AbstractMediator {

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

			int operatorId = Integer.parseInt(synContext.getProperty(
					"OPERATOR_ID").toString());
			String accessToken = synContext.getProperty("ACCESS_TOKEN")
					.toString();
			String refreshToken = synContext.getProperty("REFRESH_TOKEN")
					.toString();
			long tokenValidity = Long.parseLong(synContext.getProperty(
					"TOKEN_VALIDITY").toString());
			long tokentime = new Date().getTime();

			OparatorService operatorService = new OparatorService();
			operatorService.updateOperatorToken(operatorId, refreshToken,
					tokenValidity, tokentime, accessToken);
		} catch (Exception e) {

			log.error("error in DefaultTokenUpdaterMediator mediate : "
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
