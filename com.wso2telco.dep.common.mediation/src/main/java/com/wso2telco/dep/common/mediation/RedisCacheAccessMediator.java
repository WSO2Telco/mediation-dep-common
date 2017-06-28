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
package com.wso2telco.dep.common.mediation;

import com.wso2telco.redis.RedisClient;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

/** Sample usage:
 <class name="com.wso2telco.dep.common.mediation.RedisCacheAccessMediator">
    <property name="action" value="[SET_PACKAGE_TYPE | GET_PACKAGE_TYPE | SET_HIT_COUNT | GET_HIT_COUNT]"/>
 </class>
 */

public class RedisCacheAccessMediator extends AbstractMediator {

    private final int SET_PACKAGE_TYPE = 1;
    private final int GET_PACKAGE_TYPE = 2;
    private final int SET_HIT_COUNT = 3;
    private final int GET_HIT_COUNT = 4;

    // Default ttl value is set to 6 hours for all operators
    private final int DEFAULT_TTL = 21600;

    int operation = -1;

    public String getAction() { return null; }

    public void setAction(String action) {
        if (action.equalsIgnoreCase("SET_PACKAGE_TYPE")) {
            operation = this.SET_PACKAGE_TYPE;
        } else if (action.equalsIgnoreCase("GET_PACKAGE_TYPE")) {
            operation = this.GET_PACKAGE_TYPE;
        } else if (action.equalsIgnoreCase("SET_HIT_COUNT")) {
            operation = this.SET_HIT_COUNT;
        } else if (action.equalsIgnoreCase("GET_HIT_COUNT")) {
            operation = this.GET_HIT_COUNT;
        }
    }

    public boolean mediate(MessageContext messageContext) {
        String msisdn = messageContext.getProperty("MSISDN").toString();

        switch (operation) {
            case SET_PACKAGE_TYPE : {
                int ttl = getValidTtl(messageContext.getProperty("ttl").toString());
                try {
                    RedisClient.setKey(msisdn, messageContext
                            .getProperty("userpackagetype").toString(), ttl);
                } catch (Exception ex) {
                    log.error("Error while setting key:" + ex.getMessage());
                }

                break;
            }
            case GET_PACKAGE_TYPE : {

                try {
                    // Assign the fetched value to a messageContext property
                    messageContext.setProperty("userpackagetype", RedisClient.getKey(msisdn));
                } catch (Exception ex) {
                    log.error("Error while retrieving key:" + ex.getMessage());
                }

                break;
            }
            case SET_HIT_COUNT : {

                int hitCount = 1;
                int fraudTtl = getValidTtl(messageContext.getProperty("fraudTtl").toString());

                String existingValue = RedisClient.getKey("fraud-" + msisdn);

                if (null != existingValue) {
                    try {
                        hitCount = Integer.parseInt(RedisClient.getKey("fraud-" + msisdn)) + 1;
                    } catch (NumberFormatException nfe) {
                        log.error("Invalid value in redis client for " + "fraud-" + msisdn, nfe);
                    }
                }

                try {
                    RedisClient.setKey("fraud-" + msisdn, String.valueOf(hitCount), fraudTtl);
                } catch (Exception ex) {
                    log.error("Error while setting key:" + ex.getMessage());
                }

                break;
            }
            case GET_HIT_COUNT : {

                try {
                    // Assign the fetched value to a messageContext property
                    messageContext.setProperty("fraudValue", RedisClient.getKey("fraud-"+msisdn));
                } catch (Exception ex) {
                    log.error("Error while retrieving key:" + ex.getMessage());
                }

                break;
            }
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
