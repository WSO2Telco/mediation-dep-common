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

			String msisdn = (String) messageContext.getProperty("msisdn_value");
/*

			if(msisdn == null || msisdn.trim().equals("") ){
				return false;
			}
*/

			String pattern= (String) messageContext.getProperty("msisdn_regex");

			Pattern r = Pattern.compile(pattern);

			Matcher matcher = r.matcher(msisdn);

			if(matcher.find()){
				log.debug(" MSISDN validation passed .msisdn :"+msisdn + " , MSISDN FORMAT : "+pattern);
				messageContext.setProperty("MSISDN_VALID", "true");
				return true;
			} else {
				log.warn("invalid msisdn :"+msisdn+ ", MSISDN PATTERN :"+pattern);

				String errorText = (String) messageContext.getProperty("msisdn_paramName");

				if(errorText != null){
					errorText += " format invalid. %1";
				} else {
					errorText = "";
				}

				String errorVariable =  (String) messageContext.getProperty("msisdn_paramArray");

				if(errorVariable == null){
					errorVariable = msisdn;
				}

				ServiceError serviceError = new ServiceError("SVC0004", errorText,errorVariable,"400","SERVICE_EXCEPTION");
				messageContext.setProperty("MSISDN_VALID", "false");
				ContextModifier.getInstacnce().setError(messageContext, serviceError, msisdn);
			}
		} catch (Exception e){

			log.error("error in MSISDNValidationMediator  mediate : "
					+ e.getMessage());
			ServiceError serviceError = new ServiceError("SVC0001", "A service error occurred. Error code is %1",
					"An internal service error has occurred. Please try again later.",
					"500","SERVICE_EXCEPTION");
			messageContext.setProperty("INTERNAL_ERROR", "true");
		}

		return true;
	}
}
