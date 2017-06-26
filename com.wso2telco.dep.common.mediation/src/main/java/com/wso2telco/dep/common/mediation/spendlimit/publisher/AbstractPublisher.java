/*******************************************************************************
 * Copyright (c) 2015-2017, WSO2.Telco Inc. (http://www.wso2telco.com)
 *
 * All Rights Reserved. WSO2.Telco Inc. licences this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.wso2telco.dep.common.mediation.spendlimit.publisher;


import com.wso2telco.core.dbutils.DbService;
import com.wso2telco.dep.common.mediation.service.APIService;
import com.wso2telco.dep.common.mediation.spendlimit.entities.SpendChargeDTO;
import com.wso2telco.dep.common.mediation.spendlimit.messageenum.DataPublisherConstants;
import com.wso2telco.dep.common.mediation.spendlimit.messageenum.MessageType;
import com.wso2telco.dep.common.mediation.spendlimit.unmarshaller.GroupDTO;
import com.wso2telco.dep.common.mediation.spendlimit.unmarshaller.GroupEventUnmarshaller;
import com.wso2telco.dep.common.mediation.spendlimit.unmarshaller.OperatorNotInListException;
import org.apache.commons.logging.Log;
import org.apache.synapse.MessageContext;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

abstract class AbstractPublisher implements Publishable {

    protected Log LOG ;

    protected DbService dbservice = new DbService();
    APIService apiService = new APIService();

    protected String getCurrentDate() {
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(currentDate);
    }


    synchronized public void publish(MessageContext messageContext,
                                     JSONObject paymentRes) throws Exception {

        String consumerKey = (String) messageContext.getProperty("CONSUMER_KEY");
        GroupEventUnmarshaller unmarshaller = GroupEventUnmarshaller.getInstance();
        try {

            GroupDTO groupDTO=   unmarshaller.getGroupDTO((String)messageContext.getProperty("OPERATOR_NAME"),
                    consumerKey);
            final Long orginalPaymentTime = dbservice.getPaymentTime(MessageType.PAYMENT_RESPONSE.getMessageDid(), getRefvalue(paymentRes));

            MessageContext modifiedContext = modifyMessageContext(messageContext, orginalPaymentTime, groupDTO);
            //publish the message if not null
            if (modifiedContext != null) {

                long currentTime = System.currentTimeMillis();
                SpendChargeDTO spendChargeDTO  = new SpendChargeDTO();
                spendChargeDTO.setGroupName(groupDTO.getGroupName());
                spendChargeDTO.setOperatorId((String) messageContext.getProperty("OPERATOR_NAME"));
                spendChargeDTO.setConsumerKey(consumerKey);
                spendChargeDTO.setMsisdn((String)messageContext.getProperty("MSISDN"));
                spendChargeDTO.setCurrentTime(currentTime);

             //   if(!messageContext.getPropertyKeySet().contains("userpackagetype")||messageContext.getProperty("userpackagetype").toString().toLowerCase().equals("postpaid")) {

                    if (orginalPaymentTime > 0 && (MessageType.REFUND_RESPONSE.getMessageDid() == (Integer) messageContext.getProperty(DataPublisherConstants.PAYMENT_TYPE))) {
                        spendChargeDTO.setOrginalTime(orginalPaymentTime);
                        spendChargeDTO.setAmount(-Double.parseDouble(String.valueOf(messageContext.getProperty(DataPublisherConstants.CHARGE_AMOUNT))));
                      //  spendChargeDTO.setOperatorId((String) messageContext.getProperty("operator"));
                    } else {
                        spendChargeDTO.setAmount(Double.parseDouble(String.valueOf(messageContext.getProperty(DataPublisherConstants.CHARGE_AMOUNT))));
                        spendChargeDTO.setOrginalTime(currentTime);
                      //  spendChargeDTO.setOperatorId((String) messageContext.getProperty("operator"));
                    }
                    spendChargeDTO.setMessageType((Integer) messageContext.getProperty(DataPublisherConstants.PAYMENT_TYPE));

                    apiService.persistSpendDate(spendChargeDTO);
                    //eventsPublisherClient.publishEvent(messageContext);
                }
           // }

        } catch (OperatorNotInListException e){
            LOG.debug("NOt publish to database");
        }


    }

    abstract protected MessageContext modifyMessageContext(MessageContext messageContext,
                                                           final Long orginalPaymentTime, GroupDTO groupDTO) throws Exception;

    abstract protected String getRefvalue(JSONObject paymentRes) throws Exception;
	/*//TODO :this need to move into response handling class
	abstract protected void persistsMessage(MessageContext messageContext,
											String refvalue) throws Exception;*/

}
