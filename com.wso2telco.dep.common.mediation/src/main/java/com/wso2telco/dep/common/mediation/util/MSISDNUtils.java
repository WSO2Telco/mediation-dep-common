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

package com.wso2telco.dep.common.mediation.util;

public class MSISDNUtils {


    public static String getMSISDNSuffix(String msisdn) {
        String msisdnSuffix = msisdn;
        if (msisdn != null && !msisdn.isEmpty()) {
            if (msisdn.contains(MSISDNConstants.TEL_1)) {
                msisdnSuffix =  msisdn.replace(MSISDNConstants.TEL_1, "");
            } else if (msisdn.contains(MSISDNConstants.TEL_2)) {
                msisdnSuffix =  msisdn.replace(MSISDNConstants.TEL_2, "");
            } else if (msisdn.contains(MSISDNConstants.TEL_3)) {
                msisdnSuffix =  msisdn.replace(MSISDNConstants.TEL_3, "");
            } else if (msisdn.contains(MSISDNConstants.PLUS)) {
                msisdnSuffix =  msisdn.replace(MSISDNConstants.PLUS, "");
            }
        }
        return msisdnSuffix;
    }

}
