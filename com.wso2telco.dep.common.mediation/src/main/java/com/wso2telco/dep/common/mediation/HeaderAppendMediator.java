package com.wso2telco.dep.common.mediation;

import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import java.util.Map;

public class HeaderAppendMediator extends AbstractMediator {


    public boolean mediate(MessageContext messageContext) {
        org.apache.axis2.context.MessageContext msgContext = ((Axis2MessageContext) messageContext).getAxis2MessageContext();
        Map headersMap = (Map) msgContext.getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);

        String tokenHeaderName = (String) messageContext.getProperty("tokenHeaderName");
        String msisdn = (String) messageContext.getProperty("msisdn");
        String token = null;

        if(headersMap.containsKey(tokenHeaderName)){
            token = (String) headersMap.get(tokenHeaderName);
            msisdn = msisdn + ":" + token;
            messageContext.setProperty("msisdn",msisdn);
        }
        return true;
    }
}