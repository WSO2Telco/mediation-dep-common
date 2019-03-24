package com.wso2telco.dep.common.mediation.util;

public class MSISDNUtils {


    public static String getMSISDNSuffix(String msisdn) {
        String msisdnSuffix = msisdn;
        if (msisdn != null && !msisdn.isEmpty()) {
            if (msisdn.contains(MSISDNConstants.TEL_1)) {
                msisdnSuffix =  msisdn.replace(MSISDNConstants.TEL_1, "");
            } else if (msisdn.contains(MSISDNConstants.TEL_2)) {
                msisdnSuffix =  msisdn.replace(MSISDNConstants.TEL_2, "");
            } else if (msisdn.contains(MSISDNConstants.TEL_3)) {
                msisdnSuffix =  msisdn.replace(MSISDNConstants.TEL_3, "");
            } else if (msisdn.contains(MSISDNConstants.PLUS)) {
                msisdnSuffix =  msisdn.replace(MSISDNConstants.PLUS, "");
            }
        }
        return msisdnSuffix;
    }

}
