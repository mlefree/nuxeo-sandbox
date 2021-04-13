package com.mlefree.nuxeo.sandbox;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.annotations.RepositoryInit;

public class MleRepositoryInit implements RepositoryInit {

    @Override
    public void populate(CoreSession session) {

        // WorkSpace
        DocumentModel rootWorkspace = session.createDocumentModel("/", "Root", "Workspace");
        session.createDocument(rootWorkspace);

        // Folder
        DocumentModel folder = session.createDocumentModel(rootWorkspace.getPathAsString(), "Folder", "Folder");
        session.createDocument(folder);

        DocumentModel technicalWorkspace = session.createDocumentModel("/", "TechnicalWorkspace",
                "Workspace");
        session.createDocument(technicalWorkspace);

    }
}
