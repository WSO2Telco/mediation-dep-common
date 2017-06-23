package com.wso2telco.dep.common.mediation;

import java.nio.charset.StandardCharsets;
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

import com.wso2telco.dep.common.mediation.quotalimit.QuotaLimits;
import com.wso2telco.dep.common.mediation.service.APIService;
import com.wso2telco.dep.common.mediation.util.AttributeName;

public class QuotaLimitMediator extends AbstractMediator {

	private static Log log = LogFactory.getLog(QuotaLimitMediator.class);
	
	private String direction;
	
	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}
	
    
    public boolean mediate(MessageContext messageContext) {
        
    	String serviceProvider = null;
        String application = null;
        String apiName = null;
        String operatorName = null;
    	boolean isQuotaEnabled = false;
    	
    	try {
    		isQuotaEnabled = isQuotaEnabler(messageContext);
		} catch (AxisFault ex) {
			log.error(ex);
		}
    	
		if (isQuotaEnabled) {
		
			if (direction.equalsIgnoreCase("nb")) {
				operatorName =null;
			}else if (direction.equalsIgnoreCase("sb")) {
				operatorName=(String)messageContext.getProperty("OPERATOR_ID");			
			}
			
			serviceProvider=(String)messageContext.getProperty("USER_ID");
			application=(String)messageContext.getProperty("APPLICATION_ID");
			apiName=(String)messageContext.getProperty("API_NAME");
			
			APIService apiService = new APIService();
	    	
				try {
		            QuotaLimits quotaLimit=checkQuotaLimit(serviceProvider,application,apiName,operatorName);
		            QuotaLimits currentQuotaLimit=currentQuotaLimit(serviceProvider,application,apiName,operatorName);

		            if (quotaLimit.getSpLimit()<=currentQuotaLimit.getSpLimit()) {
		                setErrorInContext(messageContext,"POL1001","The %1 quota limit for this Service Provider has been exceeded (Current limit : "+currentQuotaLimit.getSpLimit()+")","QuotaLimit","400","POLICY_EXCEPTION");
					}
		            
		            if (quotaLimit.getAppLimit()<=currentQuotaLimit.getAppLimit()) {
		                setErrorInContext(messageContext,"POL1001","The %1 quota limit for this Application has been exceeded (Current limit : "+currentQuotaLimit.getAppLimit()+")","QuotaLimit","400","POLICY_EXCEPTION");
					}
		            
		            if (quotaLimit.getApiLimit()<=currentQuotaLimit.getApiLimit()) {
		                setErrorInContext(messageContext,"POL1001","The %1 quota limit for this API has been exceeded (Current limit : "+currentQuotaLimit.getApiLimit()+")","QuotaLimit","400","POLICY_EXCEPTION");
					}

		        } catch (Exception e) {
		            log.error("Error occurred while calling QuotaLimitCheckMediator" ,e);
		            setErrorInContext(messageContext,"SVC0001","A service error occurred. Error code is %1","An internal service error has occured. Please try again later.","500", "SERVICE_EXCEPTION");
		        }
			}
		
        return true;
    }
    
	
    private boolean isQuotaEnabler(MessageContext context) throws AxisFault {
    	   boolean quotaEnabler = false;
    	   try {
    	      org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) context).getAxis2MessageContext();
    	      Object headers = axis2MessageContext.getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
    	      if (headers != null && headers instanceof Map) {
    	         Map headersMap = (Map) headers;
    	         String jwtparam = (String) headersMap.get("x-jwt-assertion");
    	         
    	         if (jwtparam.isEmpty()) {
    	        	 setErrorInContext(context,"SVC0001","A service error occurred. Error code is %1","An internal service error has occured. Please try again later.","500", "SERVICE_EXCEPTION");
    	        	 return false;
				}
    	         
    	         String[] jwttoken = jwtparam.split("\\.");
    	         byte[] valueDecoded= Base64.getDecoder().decode(jwttoken[1]);
    	         String jwtbody = new String(valueDecoded, StandardCharsets.UTF_8);
    	         JSONObject jwtobj = new JSONObject(jwtbody);
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
    	      log.error("Error retrieve quotaEnabler",e);
    	   }

    	   return quotaEnabler;
    	}
	
	@SuppressWarnings("null")
	public QuotaLimits currentQuotaLimit(String sp,String app, String api, String operatorName) throws Exception {
		
		QuotaLimits currentQuotaLimit=new QuotaLimits();
		APIService apiService = new APIService();
		
		if (sp!=null && app!=null && api!=null) {
			currentQuotaLimit.setApiLimit(apiService.groupByApi(sp,app, api, operatorName));
		}
		if (sp!=null && app!=null && api==null){
			currentQuotaLimit.setAppLimit(apiService.groupByApplication(sp,app,operatorName));
		}
		if (sp!=null && app==null && api==null) {
			currentQuotaLimit.setSpLimit(apiService.groupBySp(sp,operatorName));
		}
		return currentQuotaLimit;		
	}
	
	public QuotaLimits checkQuotaLimit(String serviceProvider, String application, String apiName, String operatorName) throws Exception {

		APIService apiService = new APIService();
		QuotaLimits quotaLimits = new QuotaLimits();

		if (serviceProvider != null) {
			quotaLimits.setSpLimit(apiService.spLimit(serviceProvider, operatorName));
		}

		if (serviceProvider != null && application != null) {
			quotaLimits.setAppLimit(apiService.applicationLimit(serviceProvider, application, operatorName));
		}

		if (serviceProvider != null && application != null && apiName != null) {
			quotaLimits.setApiLimit(apiService.apiLimit(serviceProvider, application, apiName, operatorName));
		}

		return quotaLimits;

	}
	
    private void setErrorInContext(MessageContext synContext, String messageId,String errorText, String errorVariable, String httpStatusCode,String exceptionType) {
        synContext.setProperty("messageId", messageId);
        synContext.setProperty("errorText", errorText);
        synContext.setProperty("errorVariable", errorVariable);
        synContext.setProperty("httpStatusCode", httpStatusCode);
        synContext.setProperty("exceptionType", exceptionType);
    }
}