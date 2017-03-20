package com.wso2telco.dep.common.mediation;

import com.wso2telco.dep.operatorservice.model.OperatorEndPointDTO;
import com.wso2telco.dep.operatorservice.service.OparatorService;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import java.util.*;

public class OperatorEndpointRetrieverMediator extends AbstractMediator {

	/** The operatorEndpoints. */
	private List<OperatorEndPointDTO> operatorEndpoints;

	public OperatorEndpointRetrieverMediator() {
		try {
			operatorEndpoints = new OparatorService().getOperatorEndpoints();

		} catch (Exception e) {
			log.error("OperatorEndpointRetrieverMediator failed to initialize");
		}
	}

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

		OperatorEndPointDTO selectedEndpoint = null;

		try {
			String apiName = (String) synContext.getProperty("API_NAME");
			String operatorCode = (String) synContext.getProperty("OPERATOR_CODE");

			/**
			 * validate the operator code is not null
			 */
			if (operatorCode != null && operatorCode.trim().length() > 0) {
				log.debug("operator pick from the property OPERATOR_CODE : " + operatorCode);
			} else {
				log.debug("the request does not contain an operator code");
			}

			/**
			 * Select the endpoint by API_NAME and OPERATOR_CODE
			 */
			for (OperatorEndPointDTO endPointDTO : operatorEndpoints) {

				if ((endPointDTO.getApi().equalsIgnoreCase(apiName))
						&& (operatorCode.equalsIgnoreCase(endPointDTO.getOperatorcode()))) {

					selectedEndpoint = endPointDTO;
					break;
				}
			}

			if (selectedEndpoint == null) {
				setErrorInContext(synContext, "SVC0001",
						"A service error occurred. Error code is %1",
						"Requested service is not provisioned", "400",
						"SERVICE_EXCEPTION");
				synContext.setProperty("ENDPOINT_ERROR", "true");
				synContext.setProperty("ENDPOINT_NOT_PROVISIONED", "true");
			} else {
				String apiEndpoint = selectedEndpoint.getEndpoint();
				synContext.setProperty("OPERATOR_ENDPOINT", apiEndpoint);
				synContext.setProperty("API_ENDPOINT", apiEndpoint);
				synContext.setProperty("OPERATOR_ID", selectedEndpoint.getOperatorid());
				synContext.setProperty("OPERATOR_NAME", operatorCode.toUpperCase());
			}

		} catch (Exception e) {

			log.error("error in EndpointRetrieverMediator mediate : "
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
