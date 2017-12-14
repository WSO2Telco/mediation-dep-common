package com.wso2telco.dep.common.mediation;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.wso2telco.dep.common.mediation.util.MsisdnDTO;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MSISDValidationMediator extends AbstractMediator {

    public boolean mediate(MessageContext messageContext) {

        String regexPattern = (String) messageContext.getProperty("msisdnRegex");
        String regexGroupNumber = (String) messageContext.getProperty("msisdnRegexGroup");
        String regexPrefixNumber = (String) messageContext.getProperty("msisdnPrefixGroup");
        List<MsisdnDTO> tempValidMsisdnList = new ArrayList<MsisdnDTO>();
        List<String> validMsisdnList = new ArrayList<String>();
        List<String> invalidMsisdnList = new ArrayList<String>();
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher;
        Gson gson = new Gson();

        try {

            JsonArray msisdns = new JsonParser().parse(messageContext.getProperty("MSISDN").toString()).getAsJsonArray();

            for (int i=0;i<msisdns.size();i++) {
                matcher = pattern.matcher(msisdns.get(i).getAsString());

                if (matcher.matches()) {
                    MsisdnDTO msisdn = new MsisdnDTO(matcher.group(Integer.parseInt(regexPrefixNumber)), matcher.group(Integer.parseInt(regexGroupNumber)));

                    if (!tempValidMsisdnList.contains(msisdn)) {
                        tempValidMsisdnList.add(msisdn);
                    }
                } else {
                    invalidMsisdnList.add(msisdns.get(i).getAsString());
                }
            }

            for(MsisdnDTO msisdn : tempValidMsisdnList){
                validMsisdnList.add(msisdn.toString());
            }


            messageContext.setProperty("validMsisdns", gson.toJson(validMsisdnList));
            messageContext.setProperty("invalidMsisdns", gson.toJson(invalidMsisdnList));
            messageContext.setProperty("validationRegex", regexPattern);
            messageContext.setProperty("validationPrefixGroup",regexPrefixNumber);
            messageContext.setProperty("validationDigitsGroup",regexGroupNumber);

        } catch (Exception e) {
            log.error("Error Validating MSISDN", e);
            setErrorInContext(messageContext,"SVC0001",e.getMessage(),"","400","SERVICE_EXCEPTION");
            messageContext.setProperty("INTERNAL_ERROR","true");
        }


        return true;
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
