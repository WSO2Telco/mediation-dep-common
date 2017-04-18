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


/**
 * This mediator is used to insert a key-value pair in Redis with a ttl.
 */

public class PackageTypeCacheMediator extends AbstractMediator {

    // Default ttl value is set to 6 hours for all operators
    private static final int DEFAULT_TTL = 21600;


    public boolean mediate(MessageContext messageContext) {

        try {
            RedisClient.setKey(messageContext.getProperty("uri.var.userAddress").toString(), messageContext
                    .getProperty("userpackagetype").toString(), getValidTtl(messageContext.getProperty("ttl")
                    .toString()));

        } catch (Exception ex) {
            log.error("Error while setting key:" + ex.getMessage());
        }

        return true;

    }

    /**
     * This method checks the validity of ttl request parameter and assign default value if invalid.
     *
     * @param ttlStr
     * @return valid ttl int
     */
    private int getValidTtl(String ttlStr) {

        // If "uri.var.ttl" is valid assign ttl
        //noinspection Since15
        if (null != ttlStr && !ttlStr.isEmpty()) {

            try {
                int ttl = Integer.parseInt(ttlStr);

                if (ttl > 0)
                    return ttl;

            } catch (NumberFormatException ex) {
                log.error("ttl set to default. ttl parameter " + ttlStr + " is invalid. " + ex.getMessage());
            }
        }

        return DEFAULT_TTL;
    }
}

