package com.mlefree.nuxeo.sandbox.operations;

import static com.mlefree.nuxeo.sandbox.MleRepositoryInit.FILE_A_PATH;
import static com.mlefree.nuxeo.sandbox.MleRepositoryInit.FILE_B_PATH;
import static com.mlefree.nuxeo.sandbox.utils.WorkflowUtils.getAllWorkflows;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.routing.api.DocumentRoute;

import com.mlefree.nuxeo.sandbox.workflows.AbstractIntegrationTestWorkflow;

import junit.framework.TestCase;

public class TestWorkflowGetAllActive extends AbstractIntegrationTestWorkflow {

    @Test
    public void shouldGetAll() throws OperationException {
        List<DocumentRoute> wfs = getAllWorkflows(session);
        TestCase.assertEquals(2, wfs.size());

        DocumentModel docA = session.getDocument(new PathRef(FILE_A_PATH));
        launchWorkflow(session, docA, ACTIVE_COMPATIBLE_WF_01);

        DocumentModel docB = session.getDocument(new PathRef(FILE_B_PATH));
        launchWorkflow(session, docB, ACTIVE_NOT_COMPATIBLE_WF_01);

        OperationContext ctx = new OperationContext(session);
        DocumentModelList activeWfs = (DocumentModelList) automationService.run(ctx, WorkflowGetAllActive.ID);

        assertEquals(1, activeWfs.size());
    }

    @Test
    public void shouldGetAllWithoutFilter() throws OperationException {
        DocumentModel docA = session.getDocument(new PathRef(FILE_A_PATH));
        launchWorkflow(session, docA, ACTIVE_COMPATIBLE_WF_01);

        DocumentModel docB = session.getDocument(new PathRef(FILE_B_PATH));
        launchWorkflow(session, docB, ACTIVE_NOT_COMPATIBLE_WF_01);

        OperationContext ctx = new OperationContext(session);
        HashMap<String, String> params = new HashMap<>();
        params.put("activeOnly", "false");
        DocumentModelList activeWfs = (DocumentModelList) automationService.run(ctx, WorkflowGetAllActive.ID, params);

        assertEquals(2, activeWfs.size());
    }

}
