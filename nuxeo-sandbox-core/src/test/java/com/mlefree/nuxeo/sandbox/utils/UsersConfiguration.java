package com.mlefree.nuxeo.sandbox.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.mlefree.nuxeo.sandbox.utils.TestConstants.ADMINISTRATORS;
import static com.mlefree.nuxeo.sandbox.utils.TestConstants.FACTORIES;
import static com.mlefree.nuxeo.sandbox.utils.TestConstants.FACTORY_X;
import static com.mlefree.nuxeo.sandbox.utils.TestConstants.MEMBERS;

public enum UsersConfiguration {

    ADMIN("admin", Collections.singletonList(new UsersGroup(ADMINISTRATORS))), MEMBER_X("memberXuser",
            Arrays.asList(new UsersGroup(MEMBERS)));

    private String userName;

    private List<UsersGroup> groupList;

    UsersConfiguration(String userName, List<UsersGroup> groupList) {
        this.userName = userName;
        this.groupList = groupList;
    }

    public String getUserName() {
        return userName;
    }

    public List<UsersGroup> getGroupList() {
        return groupList;
    }

    public String[] getGroupNames() {
        return groupList.stream().map(x -> x.name).toArray(String[]::new);
    }

}
