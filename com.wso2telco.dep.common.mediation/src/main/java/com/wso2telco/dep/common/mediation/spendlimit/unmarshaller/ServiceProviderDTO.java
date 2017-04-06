package com.wso2telco.dep.common.mediation.spendlimit.unmarshaller;

import com.wso2telco.dep.common.mediation.spendlimit.entities.Application;

import java.util.ArrayList;
import java.util.List;

public class ServiceProviderDTO {

    private String spName;
    private List<Application> applicationList = new ArrayList<Application>();

    ServiceProviderDTO() {
    }

    private ServiceProviderDTO(String spName) {
        this.spName = spName;

    }

    public String getSpName() {
        return spName;
    }

    public void setSpName(String spName) {
        this.spName = spName;
    }

    public List<Application> getApplicationList() {
        return applicationList;
    }

    public void setApplicationList(List<Application> applicationList) {
        this.applicationList = applicationList;
    }

    public ServiceProviderDTO clone() {
        return new ServiceProviderDTO(this.spName);
    }
}
