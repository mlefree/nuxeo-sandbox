package com.mlefree.nuxeo.sandbox.services;

import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

import java.util.List;

public interface WorkflowService {

    void followResumeTransition(CoreSession session, DocumentModel doc);

    void setActive(CoreSession session,  DocumentModel wfDoc);

    void setOnHold(CoreSession session,  DocumentModel wfDoc);

    DocumentModelList getAllActiveWorkflow(CoreSession session);

    DocumentModelList getAllActiveTasks(CoreSession session);
}
