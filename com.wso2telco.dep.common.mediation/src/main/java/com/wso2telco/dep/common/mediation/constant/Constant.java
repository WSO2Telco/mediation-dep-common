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

package com.wso2telco.dep.common.mediation.constant;

public interface Constant {

	interface MessageContext {
		String REQUEST_ID = "REQUEST_ID";
		String PARAMVALUE = "paramValue";
		String MSISDNREGEX = "msisdnRegex";
		String PARTIALREQUESTID ="PARTIAL_REQUEST_ID";
		String ISVALIDMSISDN = "isValidMsisdn";
		String MASKED_MSISDN_MAP = "MASKED_MSISDN_MAP";
		String PROPERTY_REPLACEMENT_INDEX = "propertyReplacementIndex";
	}
	
	interface PropertyValues {
		String MSISDNCHANGE = "isValidMsisdn";
		String PARTIALREQUESTIDCHANGE = "partialRequestId";
		String UNMASK_MSISDN = "unmaskMsisdn";
		String SINGLE_PROPERTY_REPLACEMENT = "singlePropertyReplacement";
		String PROPERTY_REPLACEMENT_IN_ARRAY = "propertyReplacementInArray";
		String REPLACE_NEW_LINE_CHAR = "replaceNewLineCharacters";
	}
}
