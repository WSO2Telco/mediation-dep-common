package com.wso2telco.dep.common.mediation.spendlimit.mediator;


import com.wso2telco.core.dbutils.DBUtilException;
import com.wso2telco.core.dbutils.DbService;
import com.wso2telco.core.dbutils.dao.SpendLimitDAO;
import com.wso2telco.dep.common.mediation.spendlimit.unmarshaller.GroupDTO;
import com.wso2telco.dep.common.mediation.spendlimit.unmarshaller.GroupEventUnmarshaller;
import com.wso2telco.dep.common.mediation.spendlimit.unmarshaller.OperatorNotInListException;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.mediators.AbstractMediator;

import javax.xml.bind.JAXBException;

public class CheckLimitMediator extends AbstractMediator {

    private static GroupEventUnmarshaller groupEventUnmarshaller;
    private DbService dbservice = new DbService();

    public static GroupEventUnmarshaller loadGroupEventUnmashaller(String xml) {
        try {
            GroupEventUnmarshaller.startGroupEventUnmarshaller(xml);
        } catch (JAXBException e) {
            return null;
        }

        return GroupEventUnmarshaller.getInstance();
    }

    public boolean mediate(MessageContext mc) {
        try {
            String endUserId = mc.getProperty("endUserId").toString();
            String consumerKey = mc.getProperty("consumerKey").toString();
            String chargeAmount = mc.getProperty("amount").toString();
            String operator = mc.getProperty("OPERATOR_NAME").toString();
            String msisdn = endUserId.substring(5);
            groupEventUnmarshaller = loadGroupEventUnmashaller(mc.getProperty("spendLimitConfig").toString());
            checkSpendLimit(msisdn, operator, Double.parseDouble(chargeAmount), consumerKey, mc);
        } catch (Exception e) {
            setErrorInContext(
                    mc,
                    "SVC0001",
                    "A service error occurred. Error code is %1",
                    "An internal service error has occured. Please try again later.",
                    "500", "SERVICE_EXCEPTION");
            mc.setProperty("INTERNAL_ERROR", "true");
            throw new SynapseException(e.getMessage());
        }

        return true;
    }

    public boolean checkSpendLimit(String msisdn, String operator, Double chargeAmount, String consumerKey, MessageContext mc)
            throws DBUtilException {
        try {
            GroupDTO groupDTO = groupEventUnmarshaller.getGroupDTO(operator, consumerKey);
            Double groupdailyLimit = Double.parseDouble(groupDTO.getDayAmount());
            Double groupMonlthlyLimit = Double.parseDouble(groupDTO.getMonthAmount());
            SpendLimitDAO daySpendLimitObj = null;
            SpendLimitDAO monthSpendLimitObj = null;


            if (groupdailyLimit > 0.0) {

                if (chargeAmount <= groupdailyLimit) {
                    daySpendLimitObj = getGroupTotalDayAmount(groupDTO.getGroupName(), groupDTO.getOperator(), msisdn);
                    if (daySpendLimitObj != null && ((daySpendLimitObj.getAmount() >= groupdailyLimit) || (daySpendLimitObj.getAmount() + chargeAmount) > groupdailyLimit)) {
                        log.debug("group daily limit exceeded");
                        setErrorInContext(
                                mc,
                                "POL1001",
                                "The %1 charging limit for this user has been exceeded",
                                "daily",
                                "400", "POLICY_EXCEPTION");
                        mc.setProperty("INTERNAL_ERROR", "true");
                    }

                } else {
                    log.debug("Charge Amount exceed the limit");
                    setErrorInContext(
                            mc,
                            "POL1001",
                            "The %1 charging limit for this user has been exceeded",
                            "daily",
                            "400", "POLICY_EXCEPTION");
                    mc.setProperty("INTERNAL_ERROR", "true");
                }
            }

            if (groupMonlthlyLimit > 0.0) {

                if (chargeAmount < groupMonlthlyLimit) {
                    monthSpendLimitObj = getGroupTotalMonthAmount(groupDTO.getGroupName(), groupDTO.getOperator(), msisdn);
                    if (monthSpendLimitObj != null && (monthSpendLimitObj.getAmount() >= groupMonlthlyLimit || monthSpendLimitObj.getAmount() + chargeAmount > groupMonlthlyLimit)) {
                        log.debug("group monthly limit exceeded");
                        setErrorInContext(
                                mc,
                                "POL1001",
                                "The %1 charging limit for this user has been exceeded",
                                "monthly",
                                "400", "POLICY_EXCEPTION");
                        mc.setProperty("INTERNAL_ERROR", "true");
                    }

                } else {
                    log.debug("group monthly limit exceeded");
                    setErrorInContext(
                            mc,
                            "POL1001",
                            "The %1 charging limit for this user has been exceeded",
                            "monthly",
                            "400", "POLICY_EXCEPTION");
                    mc.setProperty("INTERNAL_ERROR", "true");
                }

            }

        } catch (OperatorNotInListException e) {
            return true;
        } catch (DBUtilException e) {
            throw new DBUtilException("Data retreving error");
        }

        return true;
    }

    private SpendLimitDAO getGroupTotalDayAmount(String groupName, String operator, String msisdn) throws
            DBUtilException {
        return dbservice.getGroupTotalDayAmount(groupName, operator, msisdn);
    }

    private SpendLimitDAO getGroupTotalMonthAmount(String groupName, String operator, String msisdn) throws
            DBUtilException {
        return dbservice.getGroupTotalMonthAmount(groupName, operator, msisdn);
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
