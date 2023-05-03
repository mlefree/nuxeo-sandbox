package com.mlefree.nuxeo.sandbox.operations;

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;

/**
 *
 */
@Operation(id=WorkflowTest.ID, category="Mle", label="WorkflowTest", description="Describe here what your operation does.")
public class WorkflowTest {

    public static final String ID = "Mle.WorkflowTest";

    @Context
    protected CoreSession session;

    @Param(name = "path", required = false)
    protected String path;

    @OperationMethod
    public DocumentModelList run(DocumentModelList docs) {

        return docs;
    }
}
