package com.mlefree.nuxeo.sandbox.services;

import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

public interface WorkflowService {

    void followResumeTransition(CoreSession session, DocumentModel doc);

    void setActive(CoreSession session, OperationContext oc, DocumentModel doc);
}
