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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import com.wso2telco.dep.common.mediation.util.ContextModifier;
import com.wso2telco.dep.common.mediation.util.ServiceError;

public class MSISDNValidationMediator extends AbstractMediator{
	{
		log = LogFactory.getLog(MSISDNValidationMediator.class);
 
	}
	final String DEFAULT_MSISDN_FORMAT="^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$"; 
	
	public boolean mediate(MessageContext messageContext) {
		
		try {
			
			String msisdn = (String) messageContext.getProperty("msisdn");
			
			//check for predefined msisdn format from the context , if not found compile with the default format
			String pattern= messageContext.getProperty("msisdn_pattern")!=null?(String)messageContext.getProperty("msisdn_pattern"):DEFAULT_MSISDN_FORMAT;
			
			 // Create a Pattern object
			Pattern r = Pattern.compile(pattern);

			
			//  create matcher object.
			Matcher m = r.matcher(msisdn);
			if (m.find( )) {
			   log.debug(" MSISDN validation passed .msisdn :"+msisdn + " , MSISDN FORMAT : "+pattern);
			   return true;
			}else {
				log.warn("invalid msisdn :"+msisdn+ ", MSISDN PATTERN :"+pattern);
				ContextModifier.getInstacnce().setError(messageContext, ServiceError.INVALID_MSISDN, msisdn);
				return false;
			}
		} catch (Exception e) {
				log.error("MSISDNValidationMediator",e);
				ContextModifier.getInstacnce().setError(messageContext, ServiceError.INTERNAL_SERVER_ERROR);
			return false;
			
		}
	    
	    
	}

}
