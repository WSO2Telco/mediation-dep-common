/*******************************************************************************
 * Copyright (c) 2015-2017, WSO2.Telco Inc. (http://www.wso2telco.com)
 *
 * All Rights Reserved. WSO2.Telco Inc. licences this file to you under the Apache License, Version 2.0 (the "License");
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
package com.wso2telco.dep.common.mediation.spendlimit.mediator;

import com.wso2telco.dep.common.mediation.spendlimit.unmarshaller.GroupDTO;
import com.wso2telco.dep.common.mediation.spendlimit.unmarshaller.GroupEventUnmarshaller;
import com.wso2telco.dep.common.mediation.spendlimit.unmarshaller.OperatorNotInListException;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import static com.wso2telco.dep.common.mediation.spendlimit.mediator.CheckLimitMediator.loadGroupEventUnmashaller;

public class CustomerInfoMediator extends AbstractMediator {

    public boolean mediate(MessageContext messageContext) {

        try {
            String operatorName = messageContext.getProperty("OPERATOR_NAME").toString();
            String consumerKey = messageContext.getProperty("CONSUMER_KEY").toString();
            String spendLimitConfig = messageContext.getProperty("spendLimitConfig").toString();
            messageContext.setProperty("customerInfoEnabled", getGroupCustomerInfo(operatorName, consumerKey,
                    spendLimitConfig));
        } catch (Exception e) {
            setErrorInContext(
                    messageContext,
                    "SVC0001",
                    "A service error occurred. Error code is %1",
                    "An internal service error has occured. Please try again later.",
                    "500", "SERVICE_EXCEPTION");
            messageContext.setProperty("INTERNAL_ERROR", "true");
        }
        return true;
    }


    private String getGroupCustomerInfo(String operatorName, String consumerKey, String config) throws
            OperatorNotInListException {
        GroupEventUnmarshaller unmarshaller = loadGroupEventUnmashaller(config);

        GroupDTO operatorGroup = unmarshaller.getGroupDTO(operatorName, consumerKey);

        return operatorGroup.getCustomerInfoEnabled();
    }

    private void setErrorInContext(MessageContext synContext, String messageId,
                                   String errorText, String errorVariable, String httpStatusCode,
                                   String exceptionType) {

        synContext.setProperty("messageId", messageId);
        synContext.setProperty("errorText", errorText);
        synContext.setProperty("errorVariable", errorVariable);
        synContext.setProperty("httpStatusCode", httpStatusCode);
        synContext.setProperty("exceptionType", exceptionType);
    }
}



