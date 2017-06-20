/*******************************************************************************
 * Copyright (c) 2015-2017, WSO2.Telco Inc. (http://www.wso2telco.com)
 *
 * All Rights Reserved. WSO2.Telco Inc. licences this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.wso2telco.dep.common.mediation.quota.limit;


import com.wso2telco.dep.common.mediation.service.APIService;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import static com.wso2telco.dep.common.mediation.quota.limit.QuotaLimits.getQuotaLimitsObj;

public class QuotaLimitCheckMediator extends AbstractMediator {


    private String serviceProvider = null;
    private String application = null;
    private String apiName = null;
    private String operatorName = null;
    private QuotaLimits quotaLimits = getQuotaLimitsObj();

    public boolean mediate(MessageContext messageContext) {

        APIService apiService = new APIService();

        serviceProvider = messageContext.getProperty("serviceProvider").toString();
        application = messageContext.getProperty("application").toString();
        apiName = messageContext.getProperty("apiName").toString();
        operatorName = messageContext.getProperty("operatorName").toString();

        try {
            apiService.checkQuotaNBLimit(serviceProvider,application,apiName,operatorName);

            messageContext.setProperty("spLimit",quotaLimits.getSpLimit());
            messageContext.setProperty("appLimit",quotaLimits.getAppLimit());
            messageContext.setProperty("apiLimit",quotaLimits.getApiLimit());


        } catch (Exception e) {

            log.error("Error occurred while calling QuotaLimitCheckMediator" ,e);
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
