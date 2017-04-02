package com.wso2telco.dep.common.mediation.spendlimit.entities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "GroupList")
public class GroupList {

    List<Group> groupList;

    public List<Group> getGroupList() {
        return groupList;
    }

    @XmlElement(name = "Group")
    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }
}
