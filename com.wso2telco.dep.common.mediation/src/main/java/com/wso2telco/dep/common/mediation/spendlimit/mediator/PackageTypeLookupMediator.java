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


import com.wso2telco.redis.RedisClient;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

public class PackageTypeLookupMediator extends AbstractMediator {

    public boolean mediate(MessageContext messageContext) {

        try {

            // Assign the fetched value to a messageContext property
            messageContext.setProperty("userpackagetype", RedisClient.getKey(messageContext.getProperty("MSISDN").toString()));

        } catch (Exception ex) {
            log.error("Error while retrieving key:" + ex.getMessage());
        }

        return true;
    }

}