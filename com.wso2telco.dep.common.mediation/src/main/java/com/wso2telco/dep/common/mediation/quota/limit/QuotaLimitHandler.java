package com.wso2telco.dep.common.mediation.quota.limit;

import java.util.Map;

import com.wso2telco.dep.common.mediation.service.APIService;
import com.wso2telco.dep.common.mediation.util.AttributeName;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.json.JSONObject;

import static com.wso2telco.dep.common.mediation.quota.limit.QuotaLimits.getQuotaLimitsObj;

public class QuotaLimitHandler extends AbstractMediator {

	private static Log log = LogFactory.getLog(QuotaLimitHandler.class);
	
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
    private QuotaLimits quotaLimits = getQuotaLimitsObj();
    
    public boolean mediate(MessageContext messageContext) {
    	
    	try {
			boolean isaggrigator = isAggregator(messageContext);
			if (isaggrigator) {
				System.out.println("isaggrigator : "+isaggrigator);
			}
		} catch (AxisFault ex) {
			ex.printStackTrace();
		}
    	
		if (operator.equalsIgnoreCase("nb")) {
			System.out.println("northbound");
		}else if (operator.equalsIgnoreCase("sb")) {
			operatorName=(String)messageContext.getProperty("OPERATOR_ID");			
		}
		
		serviceProvider=(String)messageContext.getProperty("USER_ID");
		application=(String)messageContext.getProperty("APPLICATION_ID");
		apiName=(String)messageContext.getProperty("API_NAME");
		
		APIService apiService = new APIService();
    	
		if (operator.equalsIgnoreCase("nb")) {
			operatorName =null;
		}

        try {
            Integer spQuotaLimit=apiService.checkQuotaNBLimit(serviceProvider,application,apiName,operatorName);
            
            Integer currentQuotaLimit=apiService.currentQuotaLimit(serviceProvider,application,apiName,operatorName);
            if (spQuotaLimit<=currentQuotaLimit) {
                setErrorInContext(
                		messageContext,
                        "POL1005",
                        "The %1 Quota limit for this Application or API or SP has been exceeded",
                        "QuotaLimit",
                        "400", 
                        "POLICY_EXCEPTION");
                messageContext.setProperty("INTERNAL_ERROR", "true");
			}
            messageContext.setProperty("spLimit",quotaLimits.getSpLimit());
            messageContext.setProperty("appLimit",quotaLimits.getAppLimit());
            messageContext.setProperty("apiLimit",quotaLimits.getApiLimit());


        } catch (Exception e) {

            log.error("Error occurred while calling QuotaLimitCheckMediator" ,e);
            setErrorInContext(messageContext,"SVC0001","A service error occurred. Error code is %1","An internal service error has occured. Please try again later.","500", "SERVICE_EXCEPTION");
            messageContext.setProperty("INTERNAL_ERROR", "true");
        }

        return true;
    }

    private void setErrorInContext(MessageContext synContext, String messageId,String errorText, String errorVariable, String httpStatusCode,String exceptionType) {
        synContext.setProperty("messageId", messageId);
        synContext.setProperty("errorText", errorText);
        synContext.setProperty("errorVariable", errorVariable);
        synContext.setProperty("httpStatusCode", httpStatusCode);
        synContext.setProperty("exceptionType", exceptionType);
    }
    
	@SuppressWarnings("rawtypes")
	public static boolean isAggregator(MessageContext context) throws AxisFault {
		boolean aggregator = false;
		try {
			org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) context).getAxis2MessageContext();
			Object headers = axis2MessageContext.getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
			if (headers != null && headers instanceof Map) {
				Map headersMap = (Map) headers;
				String jwtparam = (String) headersMap.get("x-jwt-assertion");
				String[] jwttoken = jwtparam.split("\\.");
				String jwtbody = Base64Coder.decodeString(jwttoken[1]);
				JSONObject jwtobj = new JSONObject(jwtbody);
				String claimaggr = jwtobj.getString("http://wso2.org/claims/role");
				if (claimaggr != null) {
					String[] allowedRoles = claimaggr.split(",");
					for (int i = 0; i < allowedRoles.length; i++) {
						if (allowedRoles[i].contains(AttributeName.AGGRIGATOR_ROLE)) {
							aggregator = true;
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			log.info("Error retrive aggregator");
		}

		return aggregator;
	}    

}