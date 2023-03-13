package com.mlefree.nuxeo.sandbox.operations;

import static com.mlefree.nuxeo.sandbox.MleRepositoryInit.FILE_A_PATH;
import static com.mlefree.nuxeo.sandbox.MleRepositoryInit.FILE_B_PATH;
import static com.mlefree.nuxeo.sandbox.utils.WorkflowUtils.getAllWorkflows;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.platform.routing.api.DocumentRoute;

import com.mlefree.nuxeo.sandbox.workflows.AbstractIntegrationTestWorkflow;
import org.nuxeo.ecm.platform.task.Task;
import org.nuxeo.ecm.platform.task.TaskService;
import org.nuxeo.runtime.api.Framework;

public class TestWorkflowTaskIsActive extends AbstractIntegrationTestWorkflow {

    @Test
    public void shouldGetTasksStatus() throws OperationException {
        List<DocumentRoute> wfs = getAllWorkflows(session);
        assertEquals(2, wfs.size());

        DocumentModel docA = session.getDocument(new PathRef(FILE_A_PATH));
        launchWorkflow(session, docA, ACTIVE_COMPATIBLE_WF_01);

        DocumentModel docB = session.getDocument(new PathRef(FILE_B_PATH));
        launchWorkflow(session, docB, ACTIVE_NOT_COMPATIBLE_WF_01);

        TaskService taskService = Framework.getService(TaskService.class);
        SortInfo sortInfo = new SortInfo("dc:created", true);
        List<Task> tasks = taskService.getAllCurrentTaskInstances(session, Collections.singletonList(sortInfo));
        assertEquals(2, tasks.size());

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(tasks.get(0).getDocument());
        boolean isTask1Active = (boolean) automationService.run(ctx, WorkflowTaskIsActive.ID);

        ctx = new OperationContext(session);
        ctx.setInput(tasks.get(1).getDocument());
        boolean isTask2Active = (boolean) automationService.run(ctx, WorkflowTaskIsActive.ID);

        assertTrue(isTask1Active);
        assertFalse(isTask2Active);
    }

}
