package com.mlefree.nuxeo.sandbox;

import static com.mlefree.nuxeo.sandbox.utils.TestConstants.MEMBERS;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoGroup;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;
import org.nuxeo.ecm.core.test.annotations.RepositoryInit;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.platform.usermanager.exceptions.UserAlreadyExistsException;
import org.nuxeo.runtime.api.Framework;

import com.mlefree.nuxeo.sandbox.utils.TestConstants;
import com.mlefree.nuxeo.sandbox.utils.TestUtils;
import com.mlefree.nuxeo.sandbox.utils.UsersConfiguration;
import com.mlefree.nuxeo.sandbox.utils.UsersGroup;

public class MleRepositoryInit implements RepositoryInit {

    private static final Log log = LogFactory.getLog(MleRepositoryInit.class);

    static public final String FILE_A_PATH = "/Root/Folder/fileA";

    static public final String FILE_B_PATH = "/Root/Folder/fileB";

    @Override
    public void populate(CoreSession session) {
        initGroupsUsersACL(session);
        createDocuments(session);
    }

    static public void initGroupsUsersACL(CoreSession session) {
        Framework.doPrivileged(() -> {
            try {
                initGroups();
                initUsers();

                ACPImpl acpRoot = new ACPImpl();
                ACL aclRoot = acpRoot.getOrCreateACL(ACL.LOCAL_ACL);
                aclRoot.addAll(Arrays.asList(new ACE(TestConstants.ADMINISTRATORS, SecurityConstants.EVERYTHING, true),
                        new ACE(TestConstants.FACTORIES, SecurityConstants.READ_WRITE, true),
                        new ACE(MEMBERS, SecurityConstants.READ, true)));
                session.getRootDocument().setACP(acpRoot, true);

                // TransactionHelper.commitOrRollbackTransaction();
                // TransactionHelper.startTransaction();
            } catch (Exception ex) {
                log.error("Exception during RepositoryInit", ex);
                throw ex;
            }
        });
    }

    static public void createDocuments(CoreSession session) {
        Framework.doPrivileged(() -> {
            try {
                // WorkSpace
                DocumentModel rootWorkspace = session.createDocumentModel("/", "Root", "Workspace");
                session.createDocument(rootWorkspace);

                // Folder
                DocumentModel folder = session.createDocumentModel(rootWorkspace.getPathAsString(), "Folder", "Folder");
                session.createDocument(folder);

                // Files
                DocumentModel fileA = TestUtils.createDummyFileDocument(session, folder.getPathAsString(), "fileA");
                assert fileA.getPathAsString().equals(FILE_A_PATH) : "Bad path " + fileA.getPathAsString();
                DocumentModel fileB = TestUtils.createDummyFileDocument(session, folder.getPathAsString(), "fileB");
                assert fileB.getPathAsString().equals(FILE_B_PATH) : "Bad path " + fileB.getPathAsString();

                session.save();
            } catch (Exception ex) {
                log.error("Exception during RepositoryInit", ex);
                throw ex;
            }
        });
    }

    static protected void initGroups() {
        Stream.of(UsersConfiguration.values()).forEach(MleRepositoryInit::createGroupsAndSubGroups);
    }

    static protected void initUsers() {
        Stream.of(UsersConfiguration.values()).forEach(MleRepositoryInit::createUser);
    }

    static protected void createGroupsAndSubGroups(UsersConfiguration userConf) {
        userConf.getGroupList().forEach(groupToCreate -> {
            createGroup(groupToCreate);
        });
    }

    static private void createGroup(UsersGroup groupToCreate) {

        if (groupToCreate.parentGroup != null) {
            createGroup(groupToCreate.parentGroup);
        }

        UserManager userManager = Framework.getService(UserManager.class);
        if (userManager.getGroup(groupToCreate.name) == null) {
            DocumentModel bareGroup = userManager.getBareGroupModel();
            bareGroup.setPropertyValue(userManager.getGroupIdField(), groupToCreate.name);
            if (groupToCreate.label != null) {
                bareGroup.setPropertyValue(userManager.getGroupLabelField(), groupToCreate.label);
            } else {
                bareGroup.setPropertyValue(userManager.getGroupLabelField(), groupToCreate.name);
            }
            userManager.createGroup(bareGroup);
        }

        if (groupToCreate.parentGroup != null) {
            NuxeoGroup parent = userManager.getGroup(groupToCreate.parentGroup.name);
            List<String> kids = parent.getMemberGroups();
            kids.add(groupToCreate.name);
            parent.setMemberGroups(kids);
            userManager.updateGroup(parent.getModel());
        }

    }

    static protected void createUser(UsersConfiguration userConf) {
        UserManager userManager = Framework.getService(UserManager.class);
        DocumentModel bareUser = userManager.getBareUserModel();
        setUserProperties(bareUser, userConf);
        try {
            userManager.createUser(bareUser);
        } catch (UserAlreadyExistsException ex) {
            DocumentModel userFound = userManager.getUserModel(userConf.getUserName());
            setUserProperties(userFound, userConf);
            userManager.updateUser(userFound);
        }
    }

    static protected void setUserProperties(DocumentModel userModel, UsersConfiguration userConf) {
        userModel.setProperty("user", "username", userConf.getUserName());
        userModel.setProperty("user", "password", userConf.getUserName());
        userModel.setProperty("user", "email", userConf.getUserName() + "@n.mo");
        userModel.setProperty("user", "groups", userConf.getGroupNames());
    }

}
