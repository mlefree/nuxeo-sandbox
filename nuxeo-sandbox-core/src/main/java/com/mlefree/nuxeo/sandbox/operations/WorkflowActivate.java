package com.mlefree.nuxeo.sandbox.operations;

import static com.mlefree.nuxeo.sandbox.services.WorkflowServiceImpl.WF_EVENTS_CATEGORY;
import static com.mlefree.nuxeo.sandbox.utils.AuditLogUtils.addAuditLog;
import static org.nuxeo.ecm.automation.core.Constants.CAT_DOCUMENT;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.DocumentRefList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.platform.routing.api.DocumentRoutingConstants;
import org.nuxeo.runtime.api.Framework;

import com.mlefree.nuxeo.sandbox.services.WorkflowService;

/**
 *
 */
@Operation(id = WorkflowActivate.ID, category = CAT_DOCUMENT, label = WorkflowActivate.ID, description = "Activate Workflow")
public class WorkflowActivate {

    protected static final Log log = LogFactory.getLog(WorkflowActivate.class);

    public static final String ID = "Mle.WorkflowActivate";

    @Context
    protected CoreSession session;

    @Context
    protected OperationContext ctx;

    @OperationMethod
    public DocumentModel run(DocumentModel wfDoc) {
        String workflowInstanceId = (String) ctx.get("workflowInstanceId");
        log.info("workflowInstanceId: " + workflowInstanceId + " " + wfDoc.getType());
        log.info("wfDoc: " + wfDoc.getId());
        if (StringUtils.isEmpty(workflowInstanceId)
                && DocumentRoutingConstants.DOCUMENT_ROUTE_DOCUMENT_TYPE.equals(wfDoc.getType())) {
            workflowInstanceId = wfDoc.getId();
        }

        if (StringUtils.isEmpty(workflowInstanceId)) {
            throw new DocumentNotFoundException("No workflow to resume");
        }

        try {
            wfDoc = session.getDocument(new IdRef(workflowInstanceId));
            WorkflowService workflowService = Framework.getService(WorkflowService.class);
            workflowService.setActive(session, wfDoc);

            String userName = session.getPrincipal().getName();
            String comment = String.format("User %s activate workflow", userName);
            addAuditLog(wfDoc, userName, "activate", WF_EVENTS_CATEGORY, comment);

        } catch (NuxeoException e) {
            log.error(String.format("Impossible to activate workflow %s (%s)", workflowInstanceId, e.getMessage()));
        }
        return wfDoc;
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
