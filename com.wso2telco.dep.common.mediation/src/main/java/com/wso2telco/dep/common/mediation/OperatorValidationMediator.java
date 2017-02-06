package com.wso2telco.dep.common.mediation;

import java.util.ArrayList;
import java.util.List;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import com.wso2telco.dep.operatorservice.service.OparatorService;
import com.wso2telco.dep.operatorservice.model.OperatorApplicationDTO;
import com.wso2telco.core.dbutils.exception.BusinessException;
import com.wso2telco.dep.operatorservice.exception.ApplicationException;
import com.wso2telco.dep.operatorservice.exception.APIException;

public class OperatorValidationMediator extends AbstractMediator {
	
	private void setErrorInContext(MessageContext synContext, String messageId, 
			String errorText, String errorVariable) {
		synContext.setProperty("messageId", messageId);
		synContext.setProperty("errorText", errorText);
		synContext.setProperty("errorVariable", errorVariable);
		synContext.setProperty("httpStatusCode", "400");
		synContext.setProperty("OPERATOR_VALIDATED", "false");
	}

	public boolean mediate(MessageContext synContext) {
		OparatorService operatorService = new OparatorService();
		
		String applicationId = (String) synContext.getProperty("APPLICATION_ID");
		
		List<OperatorApplicationDTO> validoperators = null;
		
		try {
			validoperators = operatorService.getApplicationOperators(Integer.valueOf(applicationId));
		} catch(BusinessException ex) {
			// This will be a synapse exception
			return false;
		}
		
		if (validoperators.isEmpty()) {
			setErrorInContext(synContext, "SVC0001", "A service error occurred. Error code is %1", 
					"Requested service is not provisioned");
			return true;
		}
		
		String apiName = (String) synContext.getProperty("API_NAME");
		List<Integer> activeoperators = null;
		
		try {
			activeoperators = operatorService.getActiveApplicationOperators(Integer.valueOf(applicationId), apiName);
		} catch(ApplicationException ex) {
			// This will be a synapse exception
			return false;
		} catch(APIException ex) {
			
		} catch(BusinessException ex) {
			return false;
		}
		
		List<OperatorApplicationDTO> validoperatorsDup = new ArrayList<OperatorApplicationDTO>();

		for (OperatorApplicationDTO operator : validoperators) {
			if (activeoperators.contains(operator.getOperatorid())) {
				validoperatorsDup.add(operator);
			}
		}

		if (validoperatorsDup.isEmpty()) {
			setErrorInContext(synContext, "SVC0001", "A service error occurred. Error code is %1", 
					"Requested service is not provisioned");
		}
		
		synContext.setProperty("OPERATOR_VALIDATED", "true");

		return true;
	}

}
