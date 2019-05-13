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

import org.apache.http.HttpStatus;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import com.wso2telco.dep.common.mediation.service.APIService;
import com.wso2telco.dep.common.mediation.util.AttributeName;
import com.wso2telco.dep.common.mediation.util.ContextPropertyName;
import com.wso2telco.dep.common.mediation.util.DatabaseTables;
import com.wso2telco.dep.common.mediation.util.ExceptionType;
import com.wso2telco.dep.common.mediation.util.ErrorConstants;

public class ServiceCodeRetrieverMediator extends AbstractMediator {

	public boolean mediate(MessageContext context) {
		try {
			String operatorName = (String) context.getProperty(ContextPropertyName.OPERATOR);
			String serviceName = (String) context.getProperty(ContextPropertyName.SERVICE_NAME);
			String serviceCodeRequest = (String) context.getProperty(ContextPropertyName.SERVICE_CODE);

			APIService apiService = new APIService();

			String serviceCode = apiService.getAttributeValueForCode(DatabaseTables.OPERATORS, operatorName,
					AttributeName.SERVICE_NAME, serviceName);

			if (serviceCode == null) {
				context.setProperty(ContextPropertyName.ENDPOINT_ERROR, "true");
				setErrorInContext(context, ErrorConstants.SVC0001, null,
						"No valid service code available for the service name provided.",
						String.valueOf(HttpStatus.SC_BAD_REQUEST), ExceptionType.SERVICE_EXCEPTION.toString());
			} else {
				if (serviceCodeRequest != null) {
					if (!serviceCodeRequest.equals(serviceCode)) {
						context.setProperty(ContextPropertyName.ENDPOINT_ERROR, "true");
						setErrorInContext(context, ErrorConstants.SVC0001, null,
								"Requested service code doesn't match with the service code from service name.",
								String.valueOf(HttpStatus.SC_BAD_REQUEST), ExceptionType.SERVICE_EXCEPTION.toString());
					}
				}
				context.setProperty(ContextPropertyName.SERVICE_CODE, serviceCode);
			}

		} catch (Exception ex) {
			log.error("error in EndpointRetrieverMediator mediate : " + ex.getMessage());
			setErrorInContext(context, ErrorConstants.SVC0001, "A service error occurred. Error code is %1",
					"An internal service error has occured. Please try again later.",
					String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR), ExceptionType.SERVICE_EXCEPTION.toString());
			context.setProperty(ContextPropertyName.INTERNAL_ERROR, "true");
		}
		return true;
	}

	private void setErrorInContext(MessageContext context, String messageId, String errorText, String errorVariable,
			String httpStatusCode, String exceptionType) {

		context.setProperty(ContextPropertyName.MESSAGE_ID, messageId);
		context.setProperty(ContextPropertyName.ERROR_TEXT, errorText);
		context.setProperty(ContextPropertyName.ERROR_VARIABLE, errorVariable);
		context.setProperty(ContextPropertyName.HTTP_STATUS_CODE, httpStatusCode);
		context.setProperty(ContextPropertyName.EXCEPTION_TYPE, exceptionType);
	}

}
