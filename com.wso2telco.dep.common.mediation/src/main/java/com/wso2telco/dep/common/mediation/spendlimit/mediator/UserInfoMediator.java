package com.wso2telco.dep.common.mediation.spendlimit.mediator;

import com.wso2telco.dep.common.mediation.spendlimit.unmarshaller.GroupDTO;
import com.wso2telco.dep.common.mediation.spendlimit.unmarshaller.GroupEventUnmarshaller;
import com.wso2telco.dep.common.mediation.spendlimit.unmarshaller.OperatorNotInListException;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import static com.wso2telco.dep.common.mediation.spendlimit.mediator.CheckLimitMediator.loadGroupEventUnmashaller;

public class UserInfoMediator extends AbstractMediator {
    public boolean mediate(MessageContext messageContext) {

        try {
            String operatorName = messageContext.getProperty("OPERATOR_NAME").toString();
            String consumerKey = messageContext.getProperty("CONSUMER_KEY").toString();
            String spendLimitConfig = messageContext.getProperty("spendLimitConfig").toString();
            messageContext.setProperty("userInfoEnabled", getGroupUserInfo(operatorName, consumerKey,spendLimitConfig));
        } catch (Exception e) {
            setErrorInContext(
                    messageContext,
                    "SVC0001",
                    "A service error occurred. Error code is %1",
                    "An internal service error has occured. Please try again later.",
                    "500", "SERVICE_EXCEPTION");
            messageContext.setProperty("INTERNAL_ERROR", "true");
        }

        return true;
    }


    private String getGroupUserInfo(String operatorName, String consumerKey, String config) throws OperatorNotInListException {
        GroupEventUnmarshaller unmarshaller = loadGroupEventUnmashaller(config);

        GroupDTO operatorGroup = unmarshaller.getGroupDTO(operatorName, consumerKey);

        return operatorGroup.getUserInfoEnabled();
    }

    private void setErrorInContext(MessageContext synContext, String messageId,
                                   String errorText, String errorVariable, String httpStatusCode,
                                   String exceptionType) {

        synContext.setProperty("messageId", messageId);
        synContext.setProperty("errorText", errorText);
        synContext.setProperty("errorVariable", errorVariable);
        synContext.setProperty("httpStatusCode", httpStatusCode);
        synContext.setProperty("exceptionType", exceptionType);
    }
}
