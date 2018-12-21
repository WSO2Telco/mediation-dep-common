package com.wso2telco.dep.common.mediation;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.json.JSONObject;

public class UtilMediator extends AbstractMediator {

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

	public boolean mediate(MessageContext synCtx) {
		// TODO Auto-generated method stub

		try {
			
			//System.out.println("**********************************************************");

//			if(log.isDebugEnabled()){
//				log.debug("Update payload for path, value :" + propertyPath + ", " + propertyValue);
//			}
			org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) synCtx)
					.getAxis2MessageContext();
			JSONObject jsonPayload = new JSONObject(JsonUtil.jsonPayloadToString(axis2MessageContext));
			
			switch (propertyValue) {

			case "correlatorChange":
				JSONObject objClientCorrelator = getSubPayloadObject(propertyPath, jsonPayload, "clientCorrelator");
				objClientCorrelator.remove("clientCorrelator");
				objClientCorrelator.put("clientCorrelator", synCtx.getProperty(msgContextProperty));
				JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
				break;
			case "resourceURLChange":
				JSONObject objResourceUrl = getSubPayloadObject(propertyPath, jsonPayload, "resourceURL");

				// TODO need to build logic to get resource url

				objResourceUrl.remove("resourceURL");
				objResourceUrl.put("resourceURL", "");
				JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
				break;
			case "notifyURLChange":
				JSONObject objNotifyUrl = getSubPayloadObject(propertyPath, jsonPayload, "notifyURL");

				// TODO need to build logic to get notify url

				objNotifyUrl.remove("notifyURL");
				objNotifyUrl.put("notifyURL", synCtx.getProperty(msgContextProperty));
				JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);				
				break;
			default:
				JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
			}

//			if(log.isDebugEnabled()){
//				log.debug("Updated payload for path, value :" + propertyPath + ", " + propertyValue + " :" + jsonPayload);
//			}
			
			//System.out.println("**********************************************************");

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

}
