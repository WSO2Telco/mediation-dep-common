package com.wso2telco.dep.common.mediation.spendlimit.unmarshaller;

public class ApplicationDTO {

    private String applicationName;
    private String consumerKey;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }
}
