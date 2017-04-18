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
package com.wso2telco.dep.common.mediation.spendlimit.unmarshaller;



public class OperatorNotInListException extends Exception {

    ErrorHolder error;

    public OperatorNotInListException(ErrorHolder errorHolder) {
        super("Operator not in the list");
        this.error = errorHolder;
    }


    enum ErrorHolder {
        OPERATOR_NOT_DEFINED("Operator not in the list"),
        INVALID_CONSUMER_KEY("Invalid consumer key"),
        INVALID_OPERATOR_ID("Invalid/null Operator id"),
        NO_SP_DEFINED("Service provider list not defined"),
        APPS_NOT_DEFIED("Application list not defined");

        private String str;

        ErrorHolder(String str) {
            this.str = str;
        }

        public String getDesc() {
            return this.str;
        }
    }
}