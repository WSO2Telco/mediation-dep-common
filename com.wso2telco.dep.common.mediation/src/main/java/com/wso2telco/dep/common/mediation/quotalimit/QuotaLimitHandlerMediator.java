package com.wso2telco.dep.common.mediation.quotalimit;

import java.util.Base64;
import java.util.Iterator;
import java.util.Map;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.json.JSONObject;

import com.wso2telco.dep.common.mediation.service.APIService;
import com.wso2telco.dep.common.mediation.util.AttributeName;

public class QuotaLimitHandlerMediator extends AbstractMediator {

	private static Log log = LogFactory.getLog(QuotaLimitHandlerMediator.class);
	
	private String operator;
	
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
	
    private String serviceProvider = null;
    private String application = null;
    private String apiName = null;
    private String operatorName = null;
    
    public boolean mediate(MessageContext messageContext) {
    
    	boolean isQuotaEnabler = false;
    	try {
    		isQuotaEnabler = isQuotaEnabler(messageContext);
		} catch (AxisFault ex) {
			log.error(ex);
		}
    	
		if (isQuotaEnabler) {
		
			if (operator.equalsIgnoreCase("nb")) {
				operatorName =null;
			}else if (operator.equalsIgnoreCase("sb")) {
				operatorName=(String)messageContext.getProperty("OPERATOR_ID");			
			}
			
			serviceProvider=(String)messageContext.getProperty("USER_ID");
			application=(String)messageContext.getProperty("APPLICATION_ID");
			apiName=(String)messageContext.getProperty("API_NAME");
			
			APIService apiService = new APIService();
	    	
				try {
		            QuotaLimits QuotaLimit=apiService.checkQuotaLimit(serviceProvider,application,apiName,operatorName);
		            QuotaLimits currentQuotaLimit=apiService.currentQuotaLimit(serviceProvider,application,apiName,operatorName);

		            if (QuotaLimit.getAppLimit()<=currentQuotaLimit.getAppLimit()) {
		                setErrorInContext(messageContext,"POL1001","The %1 quota limit for this Application has been exceeded","QuotaLimit","400","POLICY_EXCEPTION");
		                messageContext.setProperty("INTERNAL_ERROR", "true");
					}
		            if (QuotaLimit.getSpLimit()<=currentQuotaLimit.getSpLimit()) {
		                setErrorInContext(messageContext,"POL1001","The %1 quota limit for this Service Provider has been exceeded","QuotaLimit","400","POLICY_EXCEPTION");
		                messageContext.setProperty("INTERNAL_ERROR", "true");
					}

		            if (QuotaLimit.getApiLimit()<=currentQuotaLimit.getApiLimit()) {
		                setErrorInContext(messageContext,"POL1001","The %1 quota limit for this API has been exceeded","QuotaLimit","400","POLICY_EXCEPTION");
		                messageContext.setProperty("INTERNAL_ERROR", "true");
					}

		        } catch (Exception e) {
		            log.error("Error occurred while calling QuotaLimitCheckMediator" ,e);
		            setErrorInContext(messageContext,"SVC0001","A service error occurred. Error code is %1","An internal service error has occured. Please try again later.","500", "SERVICE_EXCEPTION");
		            messageContext.setProperty("INTERNAL_ERROR", "true");
		        }
			}
		
        return true;
    }
    
	@SuppressWarnings("rawtypes")
	public static boolean isQuotaEnabler(MessageContext context) throws AxisFault {
		boolean quotaEnabler = false;
		try {
			org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) context).getAxis2MessageContext();
			Object headers = axis2MessageContext.getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
			if (headers != null && headers instanceof Map) {
				Map headersMap = (Map) headers;
				String jwtparam = (String) headersMap.get("x-jwt-assertion");
				
				String[] jwttoken = jwtparam.split("\\.");
				byte[] valueDecoded= Base64.decodeBase64(jwttoken[1].getBytes());
				String jwtbody = new String(valueDecoded);
				JSONObject jwtobj = new JSONObject(jwtbody);
				
				/**String[] jwttoken = jwtparam.split("\\.");
				String jwtbody = Base64Coder.decodeString(jwttoken[1]);
				JSONObject jwtobj = new JSONObject(jwtbody);*/
				
				Iterator<String> keys = jwtobj.keys();
				while( keys.hasNext() ) {
					String key = (String)keys.next();
					String[] allowedRoles = jwtobj.get(key).toString().split(",");
					for (int i = 0; i < allowedRoles.length; i++) {
						if (allowedRoles[i].contains(AttributeName.QUOTA_ENABLER)) {
							quotaEnabler = true;
							break;
						}
					}
					break;
				}
			}
		} catch (Exception e) {
			log.error("Error retrive quotaEnabler");
		}

		return quotaEnabler;
	}    
	
    private void setErrorInContext(MessageContext synContext, String messageId,String errorText, String errorVariable, String httpStatusCode,String exceptionType) {
        synContext.setProperty("messageId", messageId);
        synContext.setProperty("errorText", errorText);
        synContext.setProperty("errorVariable", errorVariable);
        synContext.setProperty("httpStatusCode", httpStatusCode);
        synContext.setProperty("exceptionType", exceptionType);
    }
}