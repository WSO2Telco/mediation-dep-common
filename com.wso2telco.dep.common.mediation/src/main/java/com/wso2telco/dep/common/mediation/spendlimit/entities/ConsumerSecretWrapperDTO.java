package com.wso2telco.dep.common.mediation.spendlimit.entities;

import com.wso2telco.dep.common.mediation.spendlimit.unmarshaller.GroupDTO;
import com.wso2telco.dep.common.mediation.spendlimit.unmarshaller.ServiceProviderDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConsumerSecretWrapperDTO implements Serializable {

    private String consumerKey;

    private List<GroupDTO> consumerKeyVsGroup = new ArrayList<GroupDTO>();
    private List<ServiceProviderDTO> consumerKeyVsSp = new ArrayList<ServiceProviderDTO>();

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public List<GroupDTO> getConsumerKeyVsGroup() {
        return consumerKeyVsGroup;
    }

    public void setConsumerKeyVsGroup(List<GroupDTO> consumerKeyVsGroup) {
        this.consumerKeyVsGroup = consumerKeyVsGroup;
    }

    public List<ServiceProviderDTO> getConsumerKeyVsSp() {
        return consumerKeyVsSp;
    }

    public void setConsumerKeyVsSp(List<ServiceProviderDTO> consumerKeyVsSp) {
        this.consumerKeyVsSp = consumerKeyVsSp;
    }
}
