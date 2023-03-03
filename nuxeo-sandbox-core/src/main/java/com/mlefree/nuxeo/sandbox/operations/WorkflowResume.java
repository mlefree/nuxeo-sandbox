package com.mlefree.nuxeo.sandbox.operations;


import com.mlefree.nuxeo.sandbox.services.WorkflowService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.DocumentRefList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.runtime.api.Framework;

import static org.nuxeo.ecm.automation.core.Constants.CAT_DOCUMENT;


/**
 *
 */
@Operation(id = WorkflowResume.ID, category = CAT_DOCUMENT, label = WorkflowResume.ID, description = "Resume Workflow")
public class WorkflowResume {

    protected static final Log log = LogFactory.getLog(WorkflowResume.class);

    public static final String ID = "Mle.WorkflowResume";

    @Context
    protected CoreSession session;

    @Context
    protected OperationContext ctx;

    @OperationMethod
    public DocumentModel run(DocumentModel doc) {
        try {
            WorkflowService workflowService = Framework.getService(WorkflowService.class);
            // workflowService.followResumeTransition(session, doc);
            workflowService.setActive(session, ctx, doc);
        } catch (NuxeoException e) {
            log.error(String.format("Impossible to go back for document %s (%s)", doc.getId(), e.getMessage()));
        }
        return doc;
    }

    @OperationMethod
    public DocumentModelList run(DocumentRefList docs) {
        DocumentModelListImpl result = new DocumentModelListImpl((int) docs.totalSize());
        for (DocumentRef doc : docs) {
            result.add(run(session.getDocument(doc)));
        }
        return result;
    }

    @OperationMethod
    public DocumentModelList run(DocumentModelList docs) {
        DocumentModelListImpl result = new DocumentModelListImpl((int) docs.totalSize());
        for (DocumentModel doc : docs) {
            result.add(run(doc));
        }
        return result;
    }
}
