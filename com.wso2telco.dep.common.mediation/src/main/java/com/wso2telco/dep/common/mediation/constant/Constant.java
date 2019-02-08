package com.wso2telco.dep.common.mediation.constant;


public interface Constant {
	
	public interface JsonObject{
		
		public static final String OUTBOUNDSMSMESSAGEREQUEST = "outboundSMSMessageRequest";
		public static final String DELIVERYINFOLIST = "deliveryInfoList";
		public static final String RESOURCEURL = "resourceURL";
		public static final String CLIENTCORRELATOR = "clientCorrelator";
		public static final String NOTIFYURL = "notifyURL";
	}
	
	public interface messageContext{
		public static final String SEND_SMS_RESOURCE_URL_PREFIX = "SEND_SMS_RESOURCE_URL_PREFIX";
		public static final String RESPONSE_DELIVERY_INFO_RESOURCE_URL = "RESPONSE_DELIVERY_INFO_RESOURCE_URL";
		public static final String SEND_SMS_OPERATOR_REQUEST_ID = "SEND_SMS_OPERATOR_REQUEST_ID";
		public static final String REQUEST_ID = "REQUEST_ID";
		public static final String API_VERSION = "API_VERSION";
		public static final String GENERATED_API_ID = "GENERATED_API_ID";
		public static final String PARAMVALUE = "paramValue";
		public static final String MSISDNREGEX = "msisdnRegex";
		public static final String PARTIALREQUESTID ="PARTIAL_REQUEST_ID";
		public static final String ISVALIDMSISDN = "isValidMsisdn";
	}
	
	public interface propertyValues{
		public static final String CORRELATORCHANGE = "correlatorChange";
		public static final String RESOURCEURLCHANGE = "resourceURLChange";
		public static final String NOTIFYURLCHANGE = "notifyURLChange";
		public static final String APIVERSIONCHANGE = "generatedApiVersionChange";
		public static final String MSISDNCHANGE = "isValidMsisdn";
		public static final String PARTIALREQUESTIDCHANGE = "partialRequestId";
	}

}
