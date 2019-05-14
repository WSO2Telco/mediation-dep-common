/*******************************************************************************
 * Copyright  (c) 2015-2019, WSO2.Telco Inc. (http://www.wso2telco.com) All Rights Reserved.
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

package com.wso2telco.dep.common.mediation.constant;

public interface Constant {
	
	interface JsonObject {
		
		String OUTBOUNDSMSMESSAGEREQUEST = "outboundSMSMessageRequest";
		String DELIVERYINFOLIST = "deliveryInfoList";
		String RESOURCEURL = "resourceURL";
		String CLIENTCORRELATOR = "clientCorrelator";
		String NOTIFYURL = "notifyURL";

        String INBOUND_SMS_MESSAGE_LIST = "inboundSMSMessageList";
        String INBOUND_SMS_MESSAGE = "inboundSMSMessage";
        String MESSAGE_ID = "messageId";
        String DESTINATION_ADDR = "destinationAddress";
        String SUBSCRIPTION = "subscription";
        String DELIVERY_RECEIPT_SUBSCRIPTION = "deliveryReceiptSubscription";

        String AMOUNT_TRANSACTION = "amountTransaction";
        String PAYMENT_TRANSACTION_LIST = "paymentTransactionList";
        String RESOURCE_URL = "resourceURL";
	}
	
	interface MessageContext {
		String SEND_SMS_RESOURCE_URL_PREFIX = "SEND_SMS_RESOURCE_URL_PREFIX";
		String RESPONSE_DELIVERY_INFO_RESOURCE_URL = "RESPONSE_DELIVERY_INFO_RESOURCE_URL";
		String SEND_SMS_OPERATOR_REQUEST_ID = "SEND_SMS_OPERATOR_REQUEST_ID";
		String REQUEST_ID = "REQUEST_ID";
		String API_VERSION = "API_VERSION";
		String GENERATED_API_ID = "GENERATED_API_ID";
		String PARAMVALUE = "paramValue";
		String MSISDNREGEX = "msisdnRegex";
		String PARTIALREQUESTID ="PARTIAL_REQUEST_ID";
		String ISVALIDMSISDN = "isValidMsisdn";
        String QUERY_SMS_DELIVERY_STATUS_RESOURCE_URL = "QUERY_SMS_DELIVERY_STATUS_RESOURCE_URL";
		String GATEWAY_RESOURCE_URL_PREFIX = "GATEWAY_RESOURCE_URL_PREFIX";
		String SMS_RETRIEVE_GATEWAY_RESOURCE_URL_PREFIX = "SMS_RETRIEVE_GATEWAY_RESOURCE_URL_PREFIX";
		String CONTEXT = "CONTEXT";
		String HUB_GATEWAY = "hubGateway";
		String REST_FULL_REQUEST_PATH = "REST_FULL_REQUEST_PATH";
		String SUBSCRIPTION_ID = "subscriptionID";
		String REST_SUB_REQUEST_PATH = "REST_SUB_REQUEST_PATH";
		String OPERATOR_DELIVERY_INFO_RESOURCE_URL = "OPERATOR_DELIVERY_INFO_RESOURCE_URL";
	}
	
	interface PropertyValues {
		String CORRELATORCHANGE = "correlatorChange";
		String RESOURCEURLCHANGE = "resourceURLChange";
		String NOTIFYURLCHANGE = "notifyURLChange";
		String APIVERSIONCHANGE = "generatedApiVersionChange";
		String MSISDNCHANGE = "isValidMsisdn";
		String PARTIALREQUESTIDCHANGE = "partialRequestId";
		String SMS_MESSAGAING = "smsmessaging";
		String PAYMENT_API = "payment";
	}

	interface SequenceNames {
	    //SMS
	    String SEND_SMS_OUT_SEQ = "sendSmsOutSeq";
	    String SMS_QUERY_DELIVERY_STATUS_RESPONSE_SEQ = "smsQueryDeliveryStatusResponseSeq";
	    String SMS_RETRIEVE_OUT_SEQ = "smsRetrieveOutSeq";
	    String MODIFY_SMS_NOTIFICATION_SUBSCRIPTION_RESPONSE_PAYLOAD_SEQ = "modifySMSNotificationSubscriptionResponsePayloadSeq";
        String MODIFY_SMS_DELIVERY_SUBSCRIPTION_RESPONSE_PAYLOAD_SEQ = "modifySMSDeliverySubscriptionResponsePayloadSeq";

        //PAYMENT
        String REPLACE_RESOURCE_URL_SEQ = "replaceResourceURLSeq";
        String REPLACE_RESOURCE_URL_FOR_LIST_SEQ = "replaceResourceURLForListSeq";
	}

	interface PaymentApi {
	    String REQUEST_ID = "requestID";
    }

}
