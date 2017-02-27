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
package com.wso2telco.dep.common.mediation.util;

public enum ServiceError {
	INTERNAL_SERVER_ERROR("SVC0001","A service error occurred. Error code is %1","An internal service error has occured. Please try again later.","500", "SERVICE_EXCEPTION"),
	INVALID_MSISDN("SVC0004", "endUserId format invalid. %1", "Invalid MSISDN", "400", "SERVICE_EXCEPTION");

	ServiceError(final String messageId, String errorText, String errorVariable, String httpStatusCode,
			String exceptionType) {
		this.messageId = messageId;
		this.errorText = errorText;
		this.errorVariable = errorVariable;
		this.httpStatusCode = httpStatusCode;
		this.exceptionType = exceptionType;
	}

	private String messageId;
	private String errorText;
	private String errorVariable;
	private String httpStatusCode;
	private String exceptionType;
	
	
	public String getMessageId() {
		return messageId;
	}
	public String getErrorText() {
		return errorText;
	}
	public String getErrorVariable() {
		return errorVariable;
	}
	public String getHttpStatusCode() {
		return httpStatusCode;
	}
	public String getExceptionType() {
		return exceptionType;
	}
	
	

}
