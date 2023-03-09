package com.mlefree.nuxeo.sandbox.operations;

import static com.mlefree.nuxeo.sandbox.MleRepositoryInit.FILE_A_PATH;
import static com.mlefree.nuxeo.sandbox.MleRepositoryInit.FILE_B_PATH;
import static com.mlefree.nuxeo.sandbox.utils.WorkflowUtils.getAllWorkflows;
import static junit.framework.TestCase.assertEquals;

import java.util.List;

import org.junit.Test;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.routing.api.DocumentRoute;

import com.mlefree.nuxeo.sandbox.workflows.AbstractIntegrationTestWorkflow;

public class TestWorkflowActivate extends AbstractIntegrationTestWorkflow {

    @Test
    public void shouldActivate() throws OperationException {
        List<DocumentRoute> wfs = getAllWorkflows(session);
        assertEquals(2, wfs.size());

        DocumentModel docA = session.getDocument(new PathRef(FILE_A_PATH));
        launchWorkflow(session, docA, ACTIVE_COMPATIBLE_WF_01);

        DocumentModel docB = session.getDocument(new PathRef(FILE_B_PATH));
        launchWorkflow(session, docB, ACTIVE_NOT_COMPATIBLE_WF_01);

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(wfs.get(0).getDocument());
        automationService.run(ctx, WorkflowActivate.ID);
    }

}
