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


import com.wso2telco.dep.common.mediation.spendlimit.entities.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.*;

public class GroupEventUnmarshaller {

    private static GroupEventUnmarshaller instance;
    Log log = LogFactory.getLog(GroupEventUnmarshaller.class);
    JAXBContext jaxbContext = JAXBContext.newInstance(GroupList.class);
    Unmarshaller jaxbUnmarshaller;
    private Map<String, Set<GroupDTO>> consumerKeyVsGroup = new HashMap<String, Set<GroupDTO>>();
    private Map<String, Set<ServiceProviderDTO>> consumerKeyVsSp = new HashMap<String, Set<ServiceProviderDTO>>();
    private Map<String, GroupDTO> operatorGP = new HashMap<String, GroupDTO>();


    private GroupEventUnmarshaller(String xml) throws JAXBException {
        unmarshall(xml);
    }

    public static GroupEventUnmarshaller getInstance() {
        return instance;
    }

    public static void startGroupEventUnmarshaller(String xml) throws JAXBException {

        instance = new GroupEventUnmarshaller(xml);

    }

    private void unmarshall(String xml) throws JAXBException {

        StringReader input = new StringReader(xml);
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        GroupList groupList = (GroupList) jaxbUnmarshaller.unmarshal(input);

        for (Iterator iterator = groupList.getGroupList().iterator(); iterator.hasNext(); ) {
            Group group = (Group) iterator.next();
            GroupDTO gpDTO = new GroupDTO();

            gpDTO.setPrepaid(group.getPrepaid());

            gpDTO.setGroupName(group.getGroupName());

            gpDTO.setPostpaid(group.getPostpaid());

            gpDTO.setOperator(group.getOperator());
            gpDTO.setCustomerInfoEnabled(group.getCustomerInfoEnabled());

            operatorGP.put(group.getOperator(), gpDTO);

            for (ServiceProvider sp : group.getServiceProviderList()) {
                ServiceProviderDTO serviceProviderDTO = new ServiceProviderDTO();
                serviceProviderDTO.setSpName(sp.getSpName());


                for (Application app : sp.getApplicationList()) {
                    serviceProviderDTO.getApplicationList().add(app);
                    if (consumerKeyVsGroup.containsKey(app.getConsumerKey())) {
                        consumerKeyVsGroup.get(app.getConsumerKey()).add(gpDTO);
                    } else {
                        Set<GroupDTO> grpstack = new HashSet<GroupDTO>();
                        grpstack.add(gpDTO);
                        consumerKeyVsGroup.put(app.getConsumerKey(), grpstack);
                    }

                    if (consumerKeyVsSp.containsKey(app.getConsumerKey())) {
                        consumerKeyVsSp.get(app.getConsumerKey()).add(serviceProviderDTO);
                    } else {
                        Set<ServiceProviderDTO> spStack = new HashSet<ServiceProviderDTO>();
                        spStack.add(serviceProviderDTO);
                        consumerKeyVsSp.put(app.getConsumerKey(), spStack);
                    }


                }
                gpDTO.getServiceProviderList().add(serviceProviderDTO);
            }
        }
    }

    public ConsumerSecretWrapperDTO getGroupEventDetailDTO(final String consumerKey) throws Exception {
        if (consumerKey == null || consumerKey.trim().length() <= 0) {
            throw new Exception("Invalid consumerKey");
        }
        ConsumerSecretWrapperDTO dto = new ConsumerSecretWrapperDTO();

        dto.setConsumerKey(consumerKey.trim());

        if (consumerKeyVsGroup.get(consumerKey.trim()) != null) {
            dto.setConsumerKeyVsSp(new ArrayList<ServiceProviderDTO>(consumerKeyVsSp.get(consumerKey.trim())));
        }
        if (consumerKeyVsGroup.get(consumerKey.trim()) != null) {
            dto.setConsumerKeyVsGroup(new ArrayList<GroupDTO>(consumerKeyVsGroup.get(consumerKey.trim())));
        }
        return dto;
    }

    public GroupDTO getGroupDTO(final String operator, final String consumerKey) throws OperatorNotInListException {

        if (operator == null || operator.trim().length() <= 0) {
            throw new OperatorNotInListException(OperatorNotInListException.ErrorHolder.INVALID_OPERATOR_ID);
        }
        if (consumerKey == null || consumerKey.trim().length() <= 0) {
            throw new OperatorNotInListException(OperatorNotInListException.ErrorHolder.INVALID_CONSUMER_KEY);
        }

        if (!operatorGP.containsKey(operator.trim())) {
            throw new OperatorNotInListException(OperatorNotInListException.ErrorHolder.OPERATOR_NOT_DEFINED);
        }

        GroupDTO groupDTO = operatorGP.get(operator.trim());

        if (groupDTO.getServiceProviderList() == null || groupDTO.getServiceProviderList().isEmpty()) {
            throw new OperatorNotInListException(OperatorNotInListException.ErrorHolder.NO_SP_DEFINED);
        }

        for (ServiceProviderDTO sp : groupDTO.getServiceProviderList()) {

            if (sp.getApplicationList() == null || sp.getApplicationList().isEmpty()) {
                throw new OperatorNotInListException(OperatorNotInListException.ErrorHolder.APPS_NOT_DEFIED);
            }

            for (Application app : sp.getApplicationList()) {
                if (app.getConsumerKey().equalsIgnoreCase(consumerKey.trim())) {


                    ServiceProviderDTO retunSP = sp.clone();
                    retunSP.getApplicationList().add(app.clone());

                    GroupDTO returnDTOGP = groupDTO.clone();

                    returnDTOGP.getServiceProviderList().add(retunSP);
                    return returnDTOGP;

                }
            }

        }
        throw new OperatorNotInListException(OperatorNotInListException.ErrorHolder.OPERATOR_NOT_DEFINED);


    }

}