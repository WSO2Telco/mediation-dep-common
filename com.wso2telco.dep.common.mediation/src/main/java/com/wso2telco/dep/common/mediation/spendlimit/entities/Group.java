/*******************************************************************************
 * Copyright (c) 2015-2017, WSO2.Telco Inc. (http://www.wso2telco.com)
 *
 * All Rights Reserved. WSO2.Telco Inc. licences this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.wso2telco.dep.common.mediation.spendlimit.entities;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "Group")
public class Group {

    private String groupName;
    private String operator;
    private Prepaid prepaid;
    private Postpaid postpaid;
    private String customerInfoEnabled;
    private List<ServiceProvider> serviceProviderList;

    public Group() {
    }

    public String getGroupName() {
        return groupName;
    }

    @XmlElement(name = "GroupName")
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getOperator() {
        return operator;
    }

    @XmlElement(name = "Operator")
    public void setOperator(String operator) {
        this.operator = operator;
    }


    public Prepaid getPrepaid() {
        return prepaid;
    }

    @XmlElement(name = "Prepaid")
    public void setPrepaid(Prepaid prepaid) {
        this.prepaid = prepaid;
    }

    public Postpaid getPostpaid() {
        return postpaid;
    }

    @XmlElement(name = "Postpaid")
    public void setPostpaid(Postpaid postpaid) {
        this.postpaid = postpaid;
    }

    public String getCustomerInfoEnabled() {
        return customerInfoEnabled;
    }

    @XmlElement(name = "IsCustomerInfoEnabled")
    public void setCustomerInfoEnabled(String customerInfoEnabled) {
        this.customerInfoEnabled = customerInfoEnabled;
    }

    public List<ServiceProvider> getServiceProviderList() {
        return serviceProviderList;
    }

    @XmlElement(name = "ServiceProvider")
    public void setServiceProviderList(List<ServiceProvider> serviceProviderList) {
        this.serviceProviderList = serviceProviderList;
    }

}