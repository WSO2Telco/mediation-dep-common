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
        //do error handling here
        }

        return true;
    }


    private String getGroupUserInfo(String operatorName, String consumerKey, String config) throws OperatorNotInListException {
        GroupEventUnmarshaller unmarshaller = loadGroupEventUnmashaller(config);

        GroupDTO operatorGroup = unmarshaller.getGroupDTO(operatorName, consumerKey);

        return operatorGroup.getUserInfoEnabled();
    }
}
