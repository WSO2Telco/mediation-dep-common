package com.wso2telco.dep.common.mediation.spendlimit.unmarshaller;

public class OperatorNotInListException extends Exception {

    ErrorHolder error;

    public OperatorNotInListException(ErrorHolder errorHolder) {
        super("Operator not in the list");
        this.error = errorHolder;
    }


    enum ErrorHolder {
        OPERATOR_NOT_DEFINED("Operator not in the list"),
        INVALID_CONSUMER_KEY("Invalid consumer key"),
        INVALID_OPERATOR_ID("Invalid/null Operator id"),
        NO_SP_DEFINED("Service provider list not defined"),
        APPS_NOT_DEFIED("Application list not defined");

        private String str;

        ErrorHolder(String str) {
            this.str = str;
        }

        public String getDesc() {
            return this.str;
        }
    }
}
