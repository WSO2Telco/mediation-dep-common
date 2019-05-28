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

package com.wso2telco.dep.common.mediation;

import com.wso2telco.dep.common.mediation.constant.Constant;
import org.apache.axis2.AxisFault;
import org.apache.commons.lang.StringUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilMediator extends AbstractMediator {
    private String propertyPath;
    private String propertyName;
    private String propertyValue;
    private String msgContextProperty;

    public boolean mediate(MessageContext synCtx) {
        try {
            org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) synCtx)
                    .getAxis2MessageContext();
            JSONObject jsonPayload = new JSONObject(JsonUtil.jsonPayloadToString(axis2MessageContext));

            switch (propertyValue) {
                case Constant.PropertyValues.SINGLE_PROPERTY_REPLACEMENT:
                    JSONObject jsonSubObj = getSubPayloadObject(propertyPath, jsonPayload, propertyName);
                    jsonSubObj.put(propertyName, synCtx.getProperty(msgContextProperty));
                    JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
                    break;

                case Constant.PropertyValues.PROPERTY_REPLACEMENT_IN_ARRAY:
                    int propertyReplacementIndex = Integer.parseInt((String)synCtx.getProperty(Constant.MessageContext.PROPERTY_REPLACEMENT_INDEX));
                    String jsonPath[] = propertyPath.split("\\.");
                    jsonSubObj = null;
                    for (int i=1; i<jsonPath.length -1; i++) {
                        jsonSubObj = jsonPayload.getJSONObject(jsonPath[i]);
                    }
                    jsonSubObj.getJSONArray(jsonPath[jsonPath.length -1]).getJSONObject(propertyReplacementIndex)
                            .put(propertyName, synCtx.getProperty(msgContextProperty));
                    JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
                    break;

                case Constant.PropertyValues.MSISDNCHANGE:
                    String paramValue = (String) synCtx.getProperty(Constant.MessageContext.PARAMVALUE);
                    String regexp = (String) synCtx.getProperty(Constant.MessageContext.MSISDNREGEX);
                    boolean isValidMsisdn = isValidMsisdn(paramValue,regexp);
                    synCtx.setProperty(Constant.MessageContext.ISVALIDMSISDN, isValidMsisdn);
                    break;

                case Constant.PropertyValues.PARTIALREQUESTIDCHANGE:
                    String requestID = (String) synCtx.getProperty(Constant.MessageContext.REQUEST_ID);
                    String[] splittedParts = StringUtils.split(requestID,":");
                    String modifiedId = "";
                    if(splittedParts.length > 3) {
                        modifiedId = splittedParts[0] + ':' + splittedParts[1] + ':' + splittedParts[3];
                    }
                    synCtx.setProperty(Constant.MessageContext.PARTIALREQUESTID, modifiedId);
                    break;

                case Constant.PropertyValues.UNMASK_MSISDN:
                    String maskedMsisdn = (String) synCtx.getProperty(msgContextProperty);
                    Map<String, String> maskedMsisdnMap = (Map) synCtx.getProperty(Constant.MessageContext.MASKED_MSISDN_MAP);
                    String unmaskedMsisdn = maskedMsisdnMap.get(maskedMsisdn);
                    synCtx.setProperty(msgContextProperty, unmaskedMsisdn);
                    break;

                default:
                    JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
            }

        } catch (AxisFault axisFault) {
            log.error("Error occurred in UtilMediator mediate. " + axisFault.getMessage());
        }
        return true;
    }

    private JSONObject getSubPayloadObject(String path, JSONObject jsonPayload, String subObjPath) {
        JSONObject jsonSubObj = jsonPayload;
        List<String> arrSubPath = Arrays.asList(path.split("\\."));
        Iterator<String> iterator = arrSubPath.iterator();
        iterator.next();
        while (iterator.hasNext()) {
            String subPath = iterator.next();
            if (subPath.equals(subObjPath)) {
                break;
            } else {
                jsonSubObj = jsonSubObj.getJSONObject(subPath);
            }
        }
        return jsonSubObj;
    }

    public boolean isValidMsisdn(String paramValue, String regexp) {
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(regexp);
        matcher = pattern.matcher(paramValue);
        return matcher.matches();
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

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
}