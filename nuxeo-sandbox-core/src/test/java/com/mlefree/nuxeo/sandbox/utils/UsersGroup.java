package com.mlefree.nuxeo.sandbox.utils;

public class UsersGroup {

    public String name;

    public String label;

    public UsersGroup parentGroup;

    UsersGroup(String name) {
        this.name = name;
        this.parentGroup = null;
    }

    UsersGroup(String name, UsersGroup parentGroup) {
        this.name = name;
        this.parentGroup = parentGroup;
    }

    UsersGroup(String name, String label, UsersGroup parentGroup) {
        this.name = name;
        this.label = label;
        this.parentGroup = parentGroup;
    }
}
