package com.wso2telco.dep.common.mediation;

import com.wso2telco.dep.common.mediation.constant.Constant;
import org.apache.axis2.AxisFault;
import org.apache.commons.lang.StringUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilMediator extends AbstractMediator {
    private String propertyPath;
    private String propertyValue;
    private String msgContextProperty;
    private String apiName;
    private String sequenceName;

	public boolean mediate(MessageContext synCtx) {
		try {
			org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) synCtx)
					.getAxis2MessageContext();
			JSONObject jsonPayload = new JSONObject(JsonUtil.jsonPayloadToString(axis2MessageContext));

			switch (propertyValue) {

			case Constant.PropertyValues.CORRELATORCHANGE:
				
				JSONObject objClientCorrelator = getSubPayloadObject(propertyPath, jsonPayload,
						Constant.JsonObject.CLIENTCORRELATOR);
				objClientCorrelator.remove(Constant.JsonObject.CLIENTCORRELATOR);
				objClientCorrelator.put(Constant.JsonObject.CLIENTCORRELATOR, synCtx.getProperty(msgContextProperty));
				
				JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
				break;
				
			case Constant.PropertyValues.RESOURCEURLCHANGE:
                if (Constant.PropertyValues.SMS_MESSAGAING.equalsIgnoreCase(apiName)) {
                    changeSmsApiResourceUrl(sequenceName, jsonPayload, synCtx, axis2MessageContext);

                } else if (Constant.PropertyValues.PAYMENT_API.equalsIgnoreCase(apiName)) {
					changePaymentApiResourceUrl(sequenceName, jsonPayload, synCtx, axis2MessageContext);

                } else {
                	String errorMessage = "Error occurred in UtilMediator mediate. ResourceURL change operation is not " +
							"implemented for API: " + apiName + ". Supported APIs: [smsmessaging, payment]";
                    log.error(errorMessage);
                	throw new AxisFault(errorMessage);
                }

				break;
				
			case Constant.PropertyValues.APIVERSIONCHANGE:
				
				String apiVersion = (String) synCtx.getProperty(Constant.MessageContext.API_VERSION);
				String generatedApiVersion = apiVersion.replace(':', '_');
				
				synCtx.setProperty(Constant.MessageContext.GENERATED_API_ID, generatedApiVersion);
				break;
				
			case Constant.PropertyValues.MSISDNCHANGE:
				
				String paramValue = (String) synCtx.getProperty(Constant.MessageContext.PARAMVALUE);
				String regexp = (String) synCtx.getProperty(Constant.MessageContext.MSISDNREGEX);
				boolean isValidMsisdn = isValidMsisdn(paramValue,regexp);
				
				synCtx.setProperty(Constant.MessageContext.ISVALIDMSISDN, isValidMsisdn);
				break;
				
			case Constant.PropertyValues.PARTIALREQUESTIDCHANGE:
				 
				 String requestID = (String) synCtx.getProperty(Constant.MessageContext.REQUEST_ID);
				 String splittedParts [] = StringUtils.split(requestID,":");
				 String modifiedId = "";
				 if(splittedParts.length > 3) {
				 	modifiedId = splittedParts[0] + ':' + splittedParts[1] + ':' + splittedParts[3];
				 }
				 
				 synCtx.setProperty(Constant.MessageContext.PARTIALREQUESTID, modifiedId);
				 break;
				 
			case Constant.PropertyValues.NOTIFYURLCHANGE:
				JSONObject objNotifyUrl = getSubPayloadObject(propertyPath, jsonPayload, Constant.JsonObject.NOTIFYURL);

				// TODO need to build logic to get notify url

				objNotifyUrl.remove(Constant.JsonObject.NOTIFYURL);
				objNotifyUrl.put(Constant.JsonObject.NOTIFYURL, synCtx.getProperty(msgContextProperty));
				JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
				break;
			default:
				JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
			}

		} catch (AxisFault axisFault) {
			log.error("Error occurred in UtilMediator mediate. " + axisFault.getMessage());
		}
		return true;
	}

	private void changeSmsApiResourceUrl(String sequenceName, JSONObject jsonPayload, MessageContext context,
										 org.apache.axis2.context.MessageContext axis2MessageContext) throws AxisFault {
        String requestId;
		String resourceUrl;
		switch (sequenceName) {
            case Constant.SequenceNames.SEND_SMS_OUT_SEQ:
                String smsRetrieveResourceUrlPrefix = (String) context
                        .getProperty(Constant.MessageContext.SEND_SMS_RESOURCE_URL_PREFIX);
                requestId = (String) context.getProperty(Constant.MessageContext.REQUEST_ID);
                String request = "/request/";
                String smsRetrieveResourceUrl = (smsRetrieveResourceUrlPrefix + request + requestId);
                String responseDeliveryInfoResourceUrl = (String) context
                        .getProperty(Constant.MessageContext.RESPONSE_DELIVERY_INFO_RESOURCE_URL);
                String operatorRequestId = null;

                JSONObject jsonObject1 = (JSONObject) jsonPayload.get(Constant.JsonObject.OUTBOUNDSMSMESSAGEREQUEST);
                JSONObject jsonObject2 = (JSONObject) jsonObject1.get(Constant.JsonObject.DELIVERYINFOLIST);

                String responseResourceUrl = (String) jsonObject1.get(Constant.JsonObject.RESOURCEURL);
                operatorRequestId = responseResourceUrl.substring(responseResourceUrl.lastIndexOf('/') + 1);

                jsonObject1.remove(Constant.JsonObject.RESOURCEURL);
                jsonObject1.put(Constant.JsonObject.RESOURCEURL, smsRetrieveResourceUrl);

                jsonObject2.remove(Constant.JsonObject.RESOURCEURL);
                jsonObject2.put(Constant.JsonObject.RESOURCEURL, responseDeliveryInfoResourceUrl);

                context.setProperty(Constant.MessageContext.SEND_SMS_OPERATOR_REQUEST_ID, operatorRequestId);
                JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
                break;

            case Constant.SequenceNames.SMS_QUERY_DELIVERY_STATUS_RESPONSE_SEQ:
                resourceUrl = (String) context.getProperty(Constant.MessageContext.QUERY_SMS_DELIVERY_STATUS_RESOURCE_URL);
                JSONObject deliveryInfoListJson = jsonPayload.getJSONObject(Constant.JsonObject.DELIVERYINFOLIST);
                deliveryInfoListJson.put(Constant.JsonObject.RESOURCEURL, resourceUrl);
				JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
                break;

			case Constant.SequenceNames.SMS_RETRIEVE_OUT_SEQ:
				String gatewayResourceUrlPrefix = (String) context.getProperty(Constant.MessageContext.
						GATEWAY_RESOURCE_URL_PREFIX);
				String smsRetrieveGatewayResourceUrlPrefix = (String) context.getProperty(Constant.MessageContext.
						SMS_RETRIEVE_GATEWAY_RESOURCE_URL_PREFIX);

				String resourceUrlPrefix = null;
				if (smsRetrieveGatewayResourceUrlPrefix != null) {
					resourceUrlPrefix = smsRetrieveGatewayResourceUrlPrefix.trim();
				} else if (gatewayResourceUrlPrefix != null) {
					resourceUrlPrefix = gatewayResourceUrlPrefix.trim() +
							context.getProperty(Constant.MessageContext.CONTEXT) + "/inbound/registrations";
				} else {
					log.error("Error occurred in UtilMediator. Unable to find valid value for " +
							"SMS_RETRIEVE_GATEWAY_RESOURCE_URL_PREFIX or GATEWAY_RESOURCE_URL_PREFIX from message context");
					throw new AxisFault("Unable to find valid value for SMS_RETRIEVE_GATEWAY_RESOURCE_URL_PREFIX or " +
							"GATEWAY_RESOURCE_URL_PREFIX from message context");
				}

				requestId = (String) context.getProperty(Constant.MessageContext.REQUEST_ID);
				JSONArray messages = jsonPayload.getJSONObject(Constant.JsonObject.INBOUND_SMS_MESSAGE_LIST)
						.getJSONArray(Constant.JsonObject.INBOUND_SMS_MESSAGE);

				String registrationId = null;
				for (int i=0; i<messages.length(); i++) {
					JSONObject message = messages.getJSONObject(i);
					String messageId = message.getString(Constant.JsonObject.MESSAGE_ID);
					registrationId = message.getString(Constant.JsonObject.DESTINATION_ADDR);
					resourceUrl = resourceUrlPrefix + "/" + registrationId + "/" + requestId + "/" + messageId;
					message.put(Constant.JsonObject.RESOURCEURL, resourceUrl);
				}
				resourceUrl = resourceUrlPrefix + '/' + registrationId + '/' + requestId;
				jsonPayload.getJSONObject(Constant.JsonObject.INBOUND_SMS_MESSAGE_LIST).put(Constant.JsonObject.RESOURCEURL, resourceUrl);
				JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
				break;

			case Constant.SequenceNames.MODIFY_SMS_NOTIFICATION_SUB_RESP_PAYLOAD_SEQ:
				String hubGateway = (String) context.getProperty(Constant.MessageContext.HUB_GATEWAY);
				String requestResourceUrl = (String) context.getProperty(Constant.MessageContext.REST_FULL_REQUEST_PATH);
				String subscriptionID = (String) context.getProperty(Constant.MessageContext.SUBSCRIPTION_ID);
				resourceUrl = hubGateway + requestResourceUrl + "/" + subscriptionID;
				jsonPayload.getJSONObject(Constant.JsonObject.SUBSCRIPTION).put(Constant.JsonObject.RESOURCEURL, resourceUrl);
				JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
				break;

			case Constant.SequenceNames.SMS_RETRIEVE_NB_NOTIFICATION_SEQ:
				hubGateway = (String) context.getProperty(Constant.MessageContext.HUB_GATEWAY);
				requestResourceUrl = (String) context.getProperty(Constant.MessageContext.REST_FULL_REQUEST_PATH);
				subscriptionID = (String) context.getProperty(Constant.MessageContext.SUBSCRIPTION_ID);
				resourceUrl = hubGateway + requestResourceUrl + "/" + subscriptionID;
				jsonPayload.getJSONObject(Constant.JsonObject.DELIVERY_RECEIPT_SUBSCRIPTION)
						.put(Constant.JsonObject.RESOURCEURL, resourceUrl);
				JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
				break;

			default:
				String errorMessage = "Error occurred in UtilMediator mediate. Unknown sequenceName [" + sequenceName +
						"] provided for SMS API resourceURL change operation";
				log.error(errorMessage);
				throw new AxisFault(errorMessage);
        }
    }


    private void changePaymentApiResourceUrl(String sequenceName, JSONObject jsonPayload, MessageContext context,
											 org.apache.axis2.context.MessageContext axis2MessageContext) throws AxisFault {
		String hubGateway;
		String apiContext;
		String subResourcePath;

		switch (sequenceName) {
			case Constant.SequenceNames.REPLACE_RESOURCE_URL_SEQ:
				hubGateway = ((String) context.getProperty(Constant.MessageContext.HUB_GATEWAY)).trim();
				apiContext = ((String) context.getProperty(Constant.MessageContext.CONTEXT)).trim();
				subResourcePath = ((String) context.getProperty(Constant.MessageContext.REST_SUB_REQUEST_PATH)).trim();
				String requestID = ((String) context.getProperty(Constant.PaymentApi.REQUEST_ID)).trim();
				String resourceUrl = hubGateway + apiContext + subResourcePath +'/' + requestID;
				jsonPayload.getJSONObject(Constant.JsonObject.AMOUNT_TRANSACTION).put(Constant.JsonObject.RESOURCEURL, resourceUrl);
				JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
				break;

			case Constant.SequenceNames.REPLACE_RES_URL_FOR_LIST_SEQ:
				hubGateway = ((String) context.getProperty(Constant.MessageContext.HUB_GATEWAY)).trim();
				apiContext = ((String) context.getProperty(Constant.MessageContext.CONTEXT)).trim();
				subResourcePath = ((String) context.getProperty(Constant.MessageContext.REST_SUB_REQUEST_PATH)).trim().concat("/");
				String resourceUrlPrefix = hubGateway + apiContext + subResourcePath + "amount/";

				JSONArray transactions = jsonPayload.getJSONObject(Constant.JsonObject.PAYMENT_TRANSACTION_LIST)
						.getJSONArray(Constant.JsonObject.AMOUNT_TRANSACTION);
				for (int i=0; i<transactions.length(); i++) {
					String originalResourceURL = transactions.getJSONObject(i).getString(Constant.JsonObject.RESOURCE_URL);
					String splits[] = originalResourceURL.split("amount/");
					String resourceId = splits[1];
					transactions.getJSONObject(i).put(Constant.JsonObject.RESOURCEURL, resourceUrlPrefix + resourceId);
				}

				jsonPayload.getJSONObject(Constant.JsonObject.PAYMENT_TRANSACTION_LIST).put(Constant.JsonObject.RESOURCEURL, resourceUrlPrefix);
				JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
				break;

			default:
				String errorMessage = "Error occurred in UtilMediator mediate. Unknown sequenceName [" + sequenceName +
						"] provided for PaymentAPI resourceURL change operation";
				log.error(errorMessage);
				throw new AxisFault(errorMessage);
		}
	}


    private JSONObject getSubPayloadObject(String path, JSONObject jsonPayload, String subObjPath) {
		JSONObject objClientCorrelator = jsonPayload;
		List<String> arrSubPath = Arrays.asList(path.split("\\."));
		Iterator<String> iterator = arrSubPath.iterator();
		iterator.next();
		while (iterator.hasNext()) {
			String subPath = iterator.next();
			if (subPath.equals(subObjPath)) {
				break;
			} else {
				objClientCorrelator = objClientCorrelator.getJSONObject(subPath);
			}
		}
		return objClientCorrelator;
	}
	
	public boolean isValidMsisdn(String paramValue, String regexp) {

		Pattern pattern;
		Matcher matcher;

		pattern = Pattern.compile(regexp);
		matcher = pattern.matcher(paramValue);

		boolean isValidMsisdn = matcher.matches();

		return isValidMsisdn;
	}

	public String getPropertyPath() {
		return propertyPath;
	}

	public void setPropertyPath(String propertyPath) {
		this.propertyPath = propertyPath;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	public String getMsgContextProperty() {
		return msgContextProperty;
	}

	public void setMsgContextProperty(String msgContextProperty) {
		this.msgContextProperty = msgContextProperty;
	}

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }
}