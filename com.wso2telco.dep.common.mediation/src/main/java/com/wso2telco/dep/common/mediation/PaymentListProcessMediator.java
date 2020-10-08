package com.wso2telco.dep.common.mediation;

import org.apache.axis2.AxisFault;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.json.JSONArray;
import org.json.JSONObject;

public class PaymentListProcessMediator extends AbstractMediator {

    @Override
    public boolean mediate(MessageContext messageContext) {

        org.apache.axis2.context.MessageContext axis2MessageContext = ((Axis2MessageContext) messageContext).getAxis2MessageContext();
        JSONObject jsonPayload = new JSONObject(JsonUtil.jsonPayloadToString(axis2MessageContext));

        JSONArray amountTransactions = (JSONArray)((JSONObject)jsonPayload.get("paymentTransactionList")).get("amountTransaction");
        for (int i = 0; i < amountTransactions.length(); i++) {
            JSONObject amountTransaction = amountTransactions.getJSONObject(i);
            String urlReplacement = messageContext.getProperty("resourceUrlPrefix") + amountTransaction.getString("serverReferenceCode");
            amountTransaction.put("resourceURL", urlReplacement);
            try {
                JsonUtil.getNewJsonPayload(axis2MessageContext, jsonPayload.toString(), true, true);
            } catch (AxisFault axisFault) {
                return false;
            }
        }
        return true;
    }
}
