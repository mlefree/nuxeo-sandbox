package com.mlefree.nuxeo.sandbox.operations;

import static com.mlefree.nuxeo.sandbox.MleRepositoryInit.FILE_A_PATH;
import static com.mlefree.nuxeo.sandbox.MleRepositoryInit.FILE_B_PATH;
import static com.mlefree.nuxeo.sandbox.utils.WorkflowUtils.getAllWorkflows;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import com.mlefree.nuxeo.sandbox.workflows.AbstractIntegrationTestWorkflow;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.routing.api.DocumentRoute;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.mlefree.nuxeo.sandbox.features.MleFeature;
import com.mlefree.nuxeo.sandbox.features.StudioWorkflowIntegrationTestFeature;

import junit.framework.TestCase;

public class TestWorkflowTaskGetAllActive extends AbstractIntegrationTestWorkflow {

    @Test
    public void shouldGetAll() throws OperationException {

        DocumentModel docA = session.getDocument(new PathRef(FILE_A_PATH));
        launchWorkflow(session, docA, ACTIVE_COMPATIBLE_WF_01);

        DocumentModel docB = session.getDocument(new PathRef(FILE_B_PATH));
        launchWorkflow(session, docB, ACTIVE_NOT_COMPATIBLE_WF_01);

        OperationContext ctx = new OperationContext(session);
        DocumentModelList activeTasks = (DocumentModelList) automationService.run(ctx, WorkflowTaskGetAllActive.ID);

        assertEquals(1, activeTasks.size());

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
        DocumentModelList activeTasks = (DocumentModelList) automationService.run(ctx, WorkflowTaskGetAllActive.ID,
                params);

        assertEquals(2, activeTasks.size());

    }

}
