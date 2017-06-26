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
package com.wso2telco.dep.common.mediation.spendlimit.unmarshaller;


import com.wso2telco.dep.common.mediation.spendlimit.entities.Postpaid;
import com.wso2telco.dep.common.mediation.spendlimit.entities.Prepaid;

import java.util.ArrayList;
import java.util.List;

public class GroupDTO {

    private String groupName;
    private String operator;
    private Prepaid prepaid;
    private Postpaid postpaid;
    private String customerInfoEnabled;
    private List<ServiceProviderDTO> serviceProviderList = new ArrayList<ServiceProviderDTO>();

    GroupDTO() {

    }

    private GroupDTO(String groupName, String operator, Prepaid prepaid, Postpaid postpaid, String customerInfoEnabled) {
        this.groupName = groupName;
        this.operator = operator;
        this.prepaid = prepaid;
        this.postpaid = postpaid;
        this.customerInfoEnabled = customerInfoEnabled;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Prepaid getPrepaid() {
        return prepaid;
    }

    public void setPrepaid(Prepaid prepaid) {
        this.prepaid = prepaid;
    }

    public Postpaid getPostpaid() {
        return postpaid;
    }

    public void setPostpaid(Postpaid postpaid) {
        this.postpaid = postpaid;
    }

    public String getCustomerInfoEnabled() {
        return customerInfoEnabled;
    }

    public void setCustomerInfoEnabled(String customerInfoEnabled) {
        this.customerInfoEnabled = customerInfoEnabled;
    }

    public List<ServiceProviderDTO> getServiceProviderList() {
        return serviceProviderList;
    }

    public void setServiceProviderList(List<ServiceProviderDTO> serviceProviderList) {
        this.serviceProviderList = serviceProviderList;


    }


    public GroupDTO clone() {
        return new GroupDTO(this.groupName, this.operator, this.prepaid, this.postpaid, this.customerInfoEnabled);
    }
}