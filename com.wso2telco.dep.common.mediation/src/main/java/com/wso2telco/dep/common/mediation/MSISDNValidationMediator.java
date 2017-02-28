
/*******************************************************************************
 * Copyright  (c) 2015-2016, WSO2.Telco Inc. (http://www.wso2telco.com) All Rights Reserved.
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

import com.wso2telco.dep.common.mediation.util.ContextModifier;
import com.wso2telco.dep.common.mediation.util.ServiceError;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MSISDNValidationMediator extends AbstractMediator{

	{
		log = LogFactory.getLog(MSISDNValidationMediator.class);
	}


	public boolean mediate(MessageContext messageContext) {

		try{

			String msisdn = (String) messageContext.getProperty("MSISDN_VALUE");
			String pattern= (String) messageContext.getProperty("MSISDN_REGEX");

			Pattern r = Pattern.compile(pattern);
			Matcher matcher = r.matcher(msisdn);

			if(matcher.find()){
				log.debug(" MSISDN validation passed .msisdn :"+msisdn + " , MSISDN FORMAT : "+pattern);
				messageContext.setProperty("MSISDN_VALID", "true");
				return true;
			} else {
				log.warn("invalid msisdn :"+msisdn+ ", MSISDN PATTERN :"+pattern);

				String errorText = (String) messageContext.getProperty("MSISDN_PARAM_NAME");

				if(errorText != null){
					errorText += " format invalid. %1";
				} else {
					errorText = "";
				}

				String errorVariable =  (String) messageContext.getProperty("MSISDN_PARAM_ARRAY");

				if(errorVariable == null){
					errorVariable = msisdn;
				}

				ServiceError serviceError = new ServiceError("SVC0004", errorText,errorVariable,"400","SERVICE_EXCEPTION");
				messageContext.setProperty("MSISDN_VALID", "false");
				setError(messageContext, serviceError, msisdn);
			}
		} catch (Exception e){
			log.error("error in MSISDNValidationMediator  mediate : " + e.getMessage());
			ServiceError serviceError = new ServiceError("SVC0001", "A service error occurred. Error code is %1",
					"An internal service error has occurred. Please try again later.",
					"500","SERVICE_EXCEPTION");
			messageContext.setProperty("INTERNAL_ERROR", "true");
		}
		return true;
	}
	
	public synchronized void setError(MessageContext synContext, ServiceError error, String value) {
		synContext.setProperty("messageId", error.getMessageId());
		synContext.setProperty("errorText", error.getErrorText());
		synContext.setProperty("errorVariable", error.getErrorVariable()+value);
		synContext.setProperty("httpStatusCode", error.getHttpStatusCode());
		synContext.setProperty("exceptionType", error.getExceptionType());
	}
}
