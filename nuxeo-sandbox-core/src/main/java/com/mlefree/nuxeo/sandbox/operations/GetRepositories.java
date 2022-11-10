package com.mlefree.nuxeo.sandbox.operations;

import java.util.Collection;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.runtime.api.Framework;

@Operation(id=GetRepositories.ID, category=Constants.CAT_DOCUMENT, label="get repos", description="Describe here what your operation does.")
public class GetRepositories {

    public static final String ID = "Document.GetRepositories";

    @OperationMethod
    public Collection run() {
       Collection repos = Framework.getService(RepositoryManager.class).getRepositories();
       return repos;
    }
}
