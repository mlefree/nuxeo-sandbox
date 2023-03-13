package com.mlefree.nuxeo.sandbox.services;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

public interface WorkflowService {

    void followResumeTransition(CoreSession session, DocumentModel doc);

    void setActive(CoreSession session,  DocumentModel wfDoc);

    void setOnHold(CoreSession session,  DocumentModel wfDoc);

    DocumentModelList getAllRunningWorkflow(CoreSession session, boolean activeOnly);

    DocumentModelList getAllRunningTasks(CoreSession session, boolean activeOnly);

    boolean isTaskActive(CoreSession session, DocumentModel taskDoc);
}
