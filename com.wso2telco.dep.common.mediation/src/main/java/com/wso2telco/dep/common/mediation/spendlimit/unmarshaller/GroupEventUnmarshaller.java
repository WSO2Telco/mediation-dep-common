package com.wso2telco.dep.common.mediation.spendlimit.unmarshaller;

import com.wso2telco.dep.common.mediation.spendlimit.entities.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
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

//        ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes());


        StringReader input = new StringReader(xml);
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        GroupList groupList = (GroupList) jaxbUnmarshaller.unmarshal(input);

        for (Iterator iterator = groupList.getGroupList().iterator(); iterator.hasNext(); ) {
            Group group = (Group) iterator.next();
            GroupDTO gpDTO = new GroupDTO();
            gpDTO.setDayAmount(group.getDayAmount());
            gpDTO.setGroupName(group.getGroupName());
            gpDTO.setMonthAmount(group.getMonthAmount());
            gpDTO.setOperator(group.getOperator());
            gpDTO.setUserInfoEnabled(group.getUserInfoEnabled());

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
