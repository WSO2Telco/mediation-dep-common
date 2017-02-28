package com.wso2telco.dep.common.mediation.util;

public class ServiceError {
	/*INTERNAL_SERVER_ERROR("SVC0001","A service error occurred. Error code is %1","An internal service error has occured. Please try again later.","500", "SERVICE_EXCEPTION"),
	INVALID_MSISDN("SVC0004", "endUserId format invalid. %1", "Invalid MSISDN", "400", "SERVICE_EXCEPTION");
*/
	public ServiceError(final String messageId, String errorText, String errorVariable, String httpStatusCode,
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
