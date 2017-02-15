package com.wso2telco.dep.common.mediation;

import java.util.Date;
import java.util.List;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import com.wso2telco.dep.operatorservice.service.OparatorService;
import com.wso2telco.dep.operatorservice.model.OperatorApplicationDTO;

public class DefaultTokenRetrieverMediator extends AbstractMediator {

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

		List<OperatorApplicationDTO> validOperators = null;

		try {

			String applicationId = (String) synContext
					.getProperty("APPLICATION_ID");
			String operatorName = (String) synContext
					.getProperty("OPERATOR_NAME");
			String requestId = (String) synContext.getProperty("REQUEST_ID");
			OparatorService operatorService = new OparatorService();
			OperatorApplicationDTO operatorApplicationDTO = null;
			String token = null;

			validOperators = operatorService.getApplicationOperators(Integer
					.valueOf(applicationId));

			for (OperatorApplicationDTO dto : validOperators) {

				if (dto.getOperatorname() != null
						&& dto.getOperatorname().equalsIgnoreCase(operatorName)) {

					operatorApplicationDTO = dto;
					break;
				}
			}

			log.info("token time : " + operatorApplicationDTO.getTokentime()
					+ " request id : " + requestId);
			log.info("token validity : "
					+ operatorApplicationDTO.getTokenvalidity()
					+ " request id : " + requestId);

			long timeExpires = (long) (operatorApplicationDTO.getTokentime() + (operatorApplicationDTO
					.getTokenvalidity() * 1000));

			log.info("expire time : " + timeExpires + " request id : "
					+ requestId);

			long currentTime = new Date().getTime();

			log.info("current time : " + currentTime + " request id : "
					+ requestId);

			if (timeExpires > currentTime) {

				token = operatorApplicationDTO.getToken();
				log.info("token of " + operatorName + " operator is active"
						+ " request id : " + requestId);
				synContext.setProperty("ACCESS_TOKEN", token);
				synContext.setProperty("TOKEN_EXPIRED", "false");
				synContext.setProperty("INTERNAL_ERROR", "false");
			} else {

				log.info("regenerating the token of " + operatorName
						+ " operator" + " request id : " + requestId);

				synContext.setProperty("TOKEN_URL",
						operatorApplicationDTO.getTokenurl());
				synContext.setProperty("REFRESH_TOKEN",
						operatorApplicationDTO.getRefreshtoken());
				synContext.setProperty("TOKEN_AUTH",
						operatorApplicationDTO.getTokenauth());

				synContext.setProperty("TOKEN_EXPIRED", "true");
				synContext.setProperty("INTERNAL_ERROR", "false");
			}
		} catch (Exception e) {

			log.error("error in DefaultTokenRetrieverMediator mediate : "
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
