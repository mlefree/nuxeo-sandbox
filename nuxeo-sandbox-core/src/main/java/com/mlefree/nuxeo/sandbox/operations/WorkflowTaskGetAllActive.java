package com.mlefree.nuxeo.sandbox.operations;

import static org.nuxeo.ecm.automation.core.Constants.CAT_DOCUMENT;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.runtime.api.Framework;

import com.mlefree.nuxeo.sandbox.services.WorkflowService;

/**
 *
 */
@Operation(id = WorkflowTaskGetAllActive.ID, category = CAT_DOCUMENT, label = WorkflowTaskGetAllActive.ID, description = "Get All Active Workflows")
public class WorkflowTaskGetAllActive {

    protected static final Log log = LogFactory.getLog(WorkflowTaskGetAllActive.class);

    public static final String ID = "Mle.WorkflowTaskGetAllActive";

    @Context
    protected CoreSession session;

    @Param(name = "activeOnly", required = false)
    protected Boolean activeOnly;

    @OperationMethod
    public DocumentModelList run() {
        if (activeOnly == null) {
            activeOnly = true;
        }
        return Framework.getService(WorkflowService.class).getAllRunningTasks(session, activeOnly);
    }
}