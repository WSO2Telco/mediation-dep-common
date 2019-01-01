package com.wso2telco.dep.common.mediation;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.json.JSONObject;

import com.wso2telco.dep.common.mediation.constant.Constant;

public class UtilMediator extends AbstractMediator {

	public boolean mediate(MessageContext synCtx) {

		try {

			org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) synCtx)
					.getAxis2MessageContext();
			JSONObject jsonPayload = new JSONObject(JsonUtil.jsonPayloadToString(axis2MessageContext));

			switch (propertyValue) {

			case Constant.propertyValues.CORRELATORCHANGE:
				JSONObject objClientCorrelator = getSubPayloadObject(propertyPath, jsonPayload,
						Constant.JsonObject.CLIENTCORRELATOR);
				objClientCorrelator.remove(Constant.JsonObject.CLIENTCORRELATOR);
				objClientCorrelator.put(Constant.JsonObject.CLIENTCORRELATOR, synCtx.getProperty(msgContextProperty));
				JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
				break;

			case Constant.propertyValues.RESOURCEURLCHANGE:

				String smsRetrieveResourceUrlPrefix = (String) synCtx
						.getProperty(Constant.messageContext.SEND_SMS_RESOURCE_URL_PREFIX);
				String requestId = (String) synCtx.getProperty(Constant.messageContext.REQUEST_ID);
				String request = "/request/";
				String smsRetrieveResourceUrl = (smsRetrieveResourceUrlPrefix + request + requestId);
				String responseDeliveryInfoResourceUrl = (String) synCtx
						.getProperty(Constant.messageContext.RESPONSE_DELIVERY_INFO_RESOURCE_URL);
				String operatorRequestId = null;

				JSONObject jsonObject1 = (JSONObject) jsonPayload.get(Constant.JsonObject.OUTBOUNDSMSMESSAGEREQUEST);
				JSONObject jsonObject2 = (JSONObject) jsonObject1.get(Constant.JsonObject.DELIVERYINFOLIST);

				String responseResourceUrl = (String) jsonObject1.get(Constant.JsonObject.RESOURCEURL);
				operatorRequestId = responseResourceUrl.substring(responseResourceUrl.lastIndexOf('/') + 1);

				jsonObject1.remove(Constant.JsonObject.RESOURCEURL);
				jsonObject1.put(Constant.JsonObject.RESOURCEURL, smsRetrieveResourceUrl);

				jsonObject2.remove(Constant.JsonObject.RESOURCEURL);
				jsonObject2.put(Constant.JsonObject.RESOURCEURL, responseDeliveryInfoResourceUrl);

				synCtx.setProperty(Constant.messageContext.SEND_SMS_OPERATOR_REQUEST_ID, operatorRequestId);
				JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
				break;

			case Constant.propertyValues.NOTIFYURLCHANGE:
				JSONObject objNotifyUrl = getSubPayloadObject(propertyPath, jsonPayload, Constant.JsonObject.NOTIFYURL);

				// TODO need to build logic to get notify url

				objNotifyUrl.remove(Constant.JsonObject.NOTIFYURL);
				objNotifyUrl.put(Constant.JsonObject.NOTIFYURL, synCtx.getProperty(msgContextProperty));
				JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
				break;
			default:
				JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return true;
	}

	private JSONObject getSubPayloadObject(String path, JSONObject jsonPayload, String subObjPath) {
		JSONObject objClientCorrelator = jsonPayload;
		try {
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
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return objClientCorrelator;
	}
	
	private String propertyPath;
	private String propertyValue;
	private String msgContextProperty;

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

}
