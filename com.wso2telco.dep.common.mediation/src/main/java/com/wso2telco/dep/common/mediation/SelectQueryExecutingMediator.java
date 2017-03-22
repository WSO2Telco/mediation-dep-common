package com.wso2telco.dep.common.mediation;

import com.wso2telco.dep.common.mediation.service.APIService;
import com.wso2telco.dep.common.mediation.util.ContextPropertyName;
import com.wso2telco.dep.common.mediation.util.ExceptionType;
import com.wso2telco.dep.common.mediation.util.ServiceErrorCode;
import org.apache.http.HttpStatus;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class mediator is used for executing a custom SQL select query
 */
public class SelectQueryExecutingMediator extends AbstractMediator {

    private final String DB_SEARCH_RESULTS_PROPERTY_NAME = "DB_SEARCH_RESULT";

    private final String SQL_SELECT_QUERY_PROPERTY_NAME = "SELECT_QUERY";
    private final String SELECT_QUERY_ENRICH_BODY_PROPERTY_NAME = "SELECT_QUERY_ENRICH_BODY";
    private final String JSON_OBJECT_KEY = "RESULT_ARRAY";

    public boolean mediate(MessageContext context) {
        try {
            APIService apiService = new APIService();

            String query = (String) context.getProperty(SQL_SELECT_QUERY_PROPERTY_NAME);

            if (query == null || query.trim().equals("")) {
                handleException("SQL query is empty", context);
            }

            List<Map<String, Object>> recordList = apiService.executeCustomSelectQuery(query);

            Map<String, Object> jsonProperty = new HashMap<String, Object>();
            jsonProperty.put(JSON_OBJECT_KEY, recordList);

            JSONObject jsonObject = new JSONObject(jsonProperty);
            context.setProperty(DB_SEARCH_RESULTS_PROPERTY_NAME, jsonObject.toString());

            String enrichBody;

            try {
                enrichBody = (String) context.getProperty(SELECT_QUERY_ENRICH_BODY_PROPERTY_NAME);
                if (enrichBody.equalsIgnoreCase("true")) {
                    org.apache.axis2.context.MessageContext a2mc = ((Axis2MessageContext) context)
                            .getAxis2MessageContext();
                    JsonUtil.getNewJsonPayload(a2mc, jsonObject.toString(), true, true);
                }
            } catch (NullPointerException npe) {
                //ignore
            }
        } catch (Exception ex) {
            log.error("error in SelectQueryExecutingMediator mediate : " + ex.getMessage());
            setErrorInContext(context, ServiceErrorCode.SVC0001, "A service error occurred. Error code is %1",
                    "An internal service error has occured. Please try again later.",
                    String.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR), ExceptionType.SERVICE_EXCEPTION.toString());
            context.setProperty(ContextPropertyName.INTERNAL_ERROR, "true");
        }

        return true;
    }

    private void setErrorInContext(MessageContext context, String messageId, String errorText, String errorVariable,
                                   String httpStatusCode, String exceptionType) {

        context.setProperty(ContextPropertyName.MESSAGE_ID, messageId);
        context.setProperty(ContextPropertyName.ERROR_TEXT, errorText);
        context.setProperty(ContextPropertyName.ERROR_VARIABLE, errorVariable);
        context.setProperty(ContextPropertyName.HTTP_STATUS_CODE, httpStatusCode);
        context.setProperty(ContextPropertyName.EXCEPTION_TYPE, exceptionType);
    }

}
