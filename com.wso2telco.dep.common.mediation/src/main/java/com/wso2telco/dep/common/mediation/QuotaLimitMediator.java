package com.wso2telco.dep.common.mediation;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.json.JSONArray;
import org.json.JSONObject;

import com.wso2telco.dep.common.mediation.quotalimit.InQuotaDateRange;
import com.wso2telco.dep.common.mediation.quotalimit.QuotaLimits;
import com.wso2telco.dep.common.mediation.service.APIService;
import com.wso2telco.dep.common.mediation.util.AttributeName;

public class QuotaLimitMediator extends AbstractMediator {

	private static Log log = LogFactory.getLog(QuotaLimitMediator.class);

    public boolean mediate(MessageContext messageContext) {

    	String serviceProvider = null;
        String application = null;
        String apiName = null;
        String operatorName = null;
    	boolean isQuotaEnabled = false;

    	String direction=null;

    	try {
    		isQuotaEnabled = isQuotaEnabler(messageContext);
		} catch (AxisFault ex) {
			log.error(ex);
		}
    	direction=messageContext.getProperty("direction").toString();
    	if (isQuotaEnabled) {

			if (direction.equalsIgnoreCase("nb")) {
				operatorName =null;
			}else if (direction.equalsIgnoreCase("sb")) {
				operatorName=(String)messageContext.getProperty("OPERATOR_NAME");
			}

			serviceProvider=(String)messageContext.getProperty("USER_ID");
			application=(String)messageContext.getProperty("APPLICATION_ID");
			apiName=(String)messageContext.getProperty("API_NAME");
			Date date = new Date();
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String sqlDate = sdf.format(date);
            InQuotaDateRange inQuotaDateRange=inQuotaDateRange(serviceProvider,application,apiName,operatorName,sqlDate);
				if (!(inQuotaDateRange.getInApiQuotaDateRange() || inQuotaDateRange.getInAppQuotaDateRange() || inQuotaDateRange.getInSpQuotaDateRange())) {
					return true;
				}else {
				try {
		            QuotaLimits quotaLimit=checkQuotaLimit(serviceProvider,application,apiName,operatorName,year,month);
		            QuotaLimits currentQuotaLimit=currentQuotaLimit(serviceProvider,application,apiName,operatorName,year,month,quotaLimit);
					Integer spLimit=currentQuotaLimit.getSpLimit();
		            if (quotaLimit.getSpLimit()!=null && spLimit!=null) {
					    if (quotaLimit.getSpLimit()<=spLimit) {
					        setErrorInContext(messageContext,"POL1001","The %1 quota limit for this Service Provider has been exceeded","QuotaLimit","400","POLICY_EXCEPTION");
						}
					}

		            Integer appLimit=currentQuotaLimit.getAppLimit();
		            if (quotaLimit.getAppLimit()!=null && appLimit!=null){
			            if (quotaLimit.getAppLimit()<=appLimit) {
			                setErrorInContext(messageContext,"POL1001","The %1 quota limit for this Application has been exceeded","QuotaLimit","400","POLICY_EXCEPTION");
						}
		            }
		            Integer apiLimit=currentQuotaLimit.getApiLimit();
		            if (quotaLimit.getApiLimit()!=null && apiLimit!=null){
			            if (quotaLimit.getApiLimit()<=apiLimit) {
			                setErrorInContext(messageContext,"POL1001","The %1 quota limit for this API has been exceeded","QuotaLimit","400","POLICY_EXCEPTION");
						}
		            }

		        } catch (Exception e) {
		            log.error("Error occurred while calling QuotaLimitCheckMediator" ,e);
		            setErrorInContext(messageContext,"SVC0001","A service error occurred. Error code is %1","An internal service error has occured. Please try again later.","500", "SERVICE_EXCEPTION");
		        }

				    return true;
			}

        }else {
        return true;
    }
    }


    @SuppressWarnings("unchecked")
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
    	         JSONArray roleClaims = (JSONArray) jwtobj.get("http://wso2.org/claims/role");
    	         if(roleClaims != null) {
    	            for (int i = 0; i < roleClaims.length(); i++) {
    	            	String roleClaim = (String) roleClaims.get(i);
    	               if (roleClaim.contains(AttributeName.QUOTA_ENABLER)) {
    	                  quotaEnabler = true;
    	                  break;
    	               }
    	            }
    	         }
    	      }
    	   } catch (Exception e) {
    	      log.error("Error retrieve quotaEnabler", e);
    	   }

    	   return quotaEnabler;
    	}

	public QuotaLimits currentQuotaLimit(String sp,String app, String api, String operatorName, int year, int month, QuotaLimits quotaLimits) throws Exception {

		QuotaLimits currentQuotaLimit=new QuotaLimits();
		APIService apiService = new APIService();

		if (quotaLimits.getApiLimit() != null) {
			currentQuotaLimit.setApiLimit(apiService.groupByApi(sp,app, api, operatorName,year,month));
		}
		if (quotaLimits.getAppLimit() !=null){
			currentQuotaLimit.setAppLimit(apiService.groupByApplication(sp,app,operatorName,year,month));
		}
		if (quotaLimits.getSpLimit() !=null) {
			currentQuotaLimit.setSpLimit(apiService.groupBySp(sp,operatorName,year,month));
		}
		return currentQuotaLimit;
	}

	public QuotaLimits checkQuotaLimit(String serviceProvider, String application, String apiName, String operatorName,Integer year,Integer month) throws Exception {

		APIService apiService = new APIService();
		QuotaLimits quotaLimits = new QuotaLimits();

		if (serviceProvider != null) {
			quotaLimits.setSpLimit(apiService.spLimit(serviceProvider, operatorName,year,month));
		}

		if (serviceProvider != null && application != null) {
			quotaLimits.setAppLimit(apiService.applicationLimit(serviceProvider, application, operatorName,year,month));
		}

		if (serviceProvider != null && application != null && apiName != null) {
			quotaLimits.setApiLimit(apiService.apiLimit(serviceProvider, application, apiName, operatorName,year,month));
		}

		return quotaLimits;
    }


    private InQuotaDateRange inQuotaDateRange(String serviceProvider,String application, String apiName, String operatorName,String sqlDate)  {
    	InQuotaDateRange inQuotaDateRange=new InQuotaDateRange();
        if (serviceProvider != null) {
        	try {
        		inQuotaDateRange.setInSpQuotaDateRange(APIService.inSPQuotaDateRange(serviceProvider,operatorName,sqlDate));
			} catch (Exception e) {
				e.printStackTrace();
			}
        }

        if (serviceProvider != null && application != null) {
        	try {
        		inQuotaDateRange.setInAppQuotaDateRange(APIService.inAPPQuotaDateRange(serviceProvider,application, operatorName,sqlDate));
			} catch (Exception e) {
				e.printStackTrace();
			}
        }

        if (serviceProvider != null && application != null && apiName != null) {
        	try {
        		inQuotaDateRange.setInApiQuotaDateRange(APIService.inAPIQuotaDateRange(serviceProvider,application, apiName, operatorName,sqlDate));
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        return inQuotaDateRange;
	}

    private void setErrorInContext(MessageContext synContext, String messageId,String errorText, String errorVariable, String httpStatusCode,String exceptionType) {
        synContext.setProperty("messageId", messageId);
        synContext.setProperty("errorText", errorText);
        synContext.setProperty("errorVariable", errorVariable);
        synContext.setProperty("httpStatusCode", httpStatusCode);
        synContext.setProperty("exceptionType", exceptionType);
        synContext.setProperty("INTERNAL_ERROR", "true");
    }
}