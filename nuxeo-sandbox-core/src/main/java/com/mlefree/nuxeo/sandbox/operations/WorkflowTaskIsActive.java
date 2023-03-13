package com.mlefree.nuxeo.sandbox.operations;

import static org.nuxeo.ecm.automation.core.Constants.CAT_DOCUMENT;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.platform.task.Task;
import org.nuxeo.runtime.api.Framework;

import com.mlefree.nuxeo.sandbox.services.WorkflowService;

/**
 *
 */
@Operation(id = WorkflowTaskIsActive.ID, category = CAT_DOCUMENT, label = WorkflowTaskIsActive.ID, description = "Get Task Active Status")
public class WorkflowTaskIsActive {

    protected static final Log log = LogFactory.getLog(WorkflowTaskIsActive.class);

    public static final String ID = "Mle.WorkflowTaskIsActive";

    @Context
    protected CoreSession session;

    @OperationMethod
    public boolean run(DocumentModel taskDoc) {
        if (taskDoc == null || taskDoc.getAdapter(Task.class) == null) {
           throw new DocumentNotFoundException("Document is not a found as a Task");
        }

        return Framework.getService(WorkflowService.class).isTaskActive(session, taskDoc);
    }
}
