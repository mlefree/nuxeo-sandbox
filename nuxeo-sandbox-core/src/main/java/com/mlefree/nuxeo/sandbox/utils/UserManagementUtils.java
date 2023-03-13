package com.mlefree.nuxeo.sandbox.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.NuxeoGroup;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

public class UserManagementUtils {

    private UserManagementUtils() throws IllegalAccessException {
        throw new IllegalAccessException("Should not be instantiated.");
    }

    public static final BiPredicate<List<String>, String> GROUP_MATCH = (allGroups, groupToMatch) -> {
        if (groupToMatch == null || groupToMatch.isEmpty()) {
            return true;
        }

        if (allGroups == null) {
            return false;
        }

        return allGroups.stream().distinct().anyMatch(input -> input.equalsIgnoreCase(groupToMatch.toLowerCase()));
    };

    public static final BiPredicate<List<String>, List<String>> ONE_GROUP_MATCH = (sourceGroups, targetGroups) -> {
        if (targetGroups == null || targetGroups.isEmpty()) {
            return true;
        }

        if (sourceGroups == null) {
            return false;
        }

        for (String targetGroup : targetGroups) {
            for (String sourceGroup : sourceGroups) {
                if (sourceGroup.equals(targetGroup)) {
                    return true;
                }
            }
        }
        return false;
    };

    public static final BiPredicate<List<String>, String> GROUP_TYPE_MATCH = (allGroups, typePrefix) -> {
        if (typePrefix == null || typePrefix.isEmpty()) {
            return true;
        }

        if (allGroups == null) {
            return false;
        }

        return allGroups.stream()
                        .distinct()
                        .anyMatch(input -> input.toLowerCase().startsWith(typePrefix.toLowerCase()));
    };

    public static final BiPredicate<List<String>, String> GROUP_TYPE_ENDS_MATCH = (allGroups, typeSuffix) -> {
        if (typeSuffix == null || typeSuffix.isEmpty()) {
            return true;
        }

        if (allGroups == null) {
            return false;
        }

        return allGroups.stream().distinct().anyMatch(input -> input.toLowerCase().endsWith(typeSuffix.toLowerCase()));
    };

    public static final BiPredicate<List<String>, String> GROUP_TYPE_REGEX_MATCH = (allGroups, regex) -> {
        if (regex == null || regex.isEmpty()) {
            return false;
        }

        if (allGroups == null) {
            return false;
        }

        return allGroups.stream().distinct().anyMatch(input -> input.toLowerCase().matches(regex));
    };

    public static final BiPredicate<List<String>, Set<String>> ALL_GROUP_NAMES_MATCH = (userGroupNames,
            groupsToVerify) -> {
        if (groupsToVerify == null || groupsToVerify.isEmpty()) {
            return true;
        }

        if (userGroupNames == null) {
            return false;
        }

        return userGroupNames.containsAll(groupsToVerify);
    };

    public static Set<String> getGroupsLabelsFromGroupIds(Set<String> groups) {
        UserManager userManager = Framework.getService(UserManager.class);
        Set<String> groupsLabels = new HashSet<>();

        for (String group : groups) {
            NuxeoGroup nuxeoGroup = userManager.getGroup(group);
            groupsLabels.add(nuxeoGroup.getLabel());
        }

        return groupsLabels;
    }

    public static CloseableCoreSession openSessionAsSystem() {
        return CoreInstance.openCoreSessionSystem(null);
    }

}
