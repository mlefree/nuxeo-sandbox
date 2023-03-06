package com.mlefree.nuxeo.sandbox.workflows;

import static com.mlefree.nuxeo.sandbox.MleRepositoryInit.FILE_A_PATH;
import static com.mlefree.nuxeo.sandbox.features.MleFeature.openSessionAsUser;
import static com.mlefree.nuxeo.sandbox.features.MleFeature.waitForAsyncExec;
import static com.mlefree.nuxeo.sandbox.features.StudioWorkflowFeature.assertYourPublishWorkflowIsFinished;
import static com.mlefree.nuxeo.sandbox.features.StudioWorkflowFeature.followWorkflowTransition;
import static com.mlefree.nuxeo.sandbox.features.StudioWorkflowFeature.startWorkflowAndGetRouteId;
import static com.mlefree.nuxeo.sandbox.services.WorkflowServiceImpl.WF_ACTIVE_SECONDS_COUNT;
import static com.mlefree.nuxeo.sandbox.utils.UsersConfiguration.ADMIN;
import static com.mlefree.nuxeo.sandbox.utils.UsersConfiguration.MEMBER_X;
import static com.mlefree.nuxeo.sandbox.utils.WorkflowUtils.getDocumentRelatedWorkflows;
import static com.mlefree.nuxeo.sandbox.utils.WorkflowUtils.getWorkflowVariables;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.mlefree.nuxeo.sandbox.operations.WorkflowGetAllActive;
import com.mlefree.nuxeo.sandbox.operations.WorkflowTaskGetAllActive;
import org.junit.Test;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.routing.api.DocumentRoute;

import com.mlefree.nuxeo.sandbox.operations.WorkflowActivate;
import com.mlefree.nuxeo.sandbox.operations.WorkflowResume;
import com.mlefree.nuxeo.sandbox.utils.TestUtils;

public class TestWorkflowActivateResume extends AbstractIntegrationTestWorkflow {

    protected void shouldLaunchWorkflowActivateResumeAs(CoreSession anySession) throws OperationException {

        assertAllActiveWorkflows(anySession, 0);
        assertAllActiveTasks(anySession, 0);

        // 1) launch any ACTIVE compatible WF and verify its "active" by default
        DocumentModel doc = anySession.getDocument(new PathRef(FILE_A_PATH));
        DocumentRoute route = launchWorkflow(anySession, doc, ACTIVE_COMPATIBLE_WF_01);
        Map<String, Serializable> workflowVariables = getWorkflowVariables(route.getDocument());
        assertEquals(true, workflowVariables.get("active"));
        assertEquals(null, workflowVariables.get(WF_ACTIVE_SECONDS_COUNT));
        assertAllActiveWorkflows(anySession, 1);
        assertAllActiveTasks(anySession, 1);

        // 2) follow steps + resume/activate randomly, and verify consistency

        // step1 - on hold
        followWorkflowTransition(doc, anySession, "step1");
        resumeWorkflow(anySession, route.getDocument());
        workflowVariables = getWorkflowVariables(route.getDocument());
        assertEquals(false, workflowVariables.get("active"));
        assertAuditActiveAndResumeCount(route, 1, 1);
        assertEquals((long) 1, workflowVariables.get(WF_ACTIVE_SECONDS_COUNT));
        assertAllActiveWorkflows(anySession, 0);
        assertAllActiveTasks(anySession, 0);

        // step 1 - active
        activateWorkflow(anySession, route.getDocument());
        workflowVariables = getWorkflowVariables(route.getDocument());
        assertEquals(true, workflowVariables.get("active"));
        assertAuditActiveAndResumeCount(route, 2, 1);
        assertEquals((long) 1, workflowVariables.get(WF_ACTIVE_SECONDS_COUNT));
        assertAllActiveWorkflows(anySession, 1);
        assertAllActiveTasks(anySession, 1);

        // step1 - resume
        resumeWorkflow(anySession, route.getDocument());
        assertAuditActiveAndResumeCount(route, 2, 2);

        // step 2
        followWorkflowTransition(doc, anySession, "step2");
        workflowVariables = getWorkflowVariables(route.getDocument());
        assertEquals(false, workflowVariables.get("active"));
        assertEquals((long) 2, workflowVariables.get(WF_ACTIVE_SECONDS_COUNT));
        assertAllActiveWorkflows(anySession, 0);
        assertAllActiveTasks(anySession, 0);

        // 3) finish WF and verify that WF is not active anymore
        followWorkflowTransition(doc, anySession, "finish");
        assertYourPublishWorkflowIsFinished(route.getDocument().getId(), doc, anySession);
        assertEquals(0, getDocumentRelatedWorkflows(doc, anySession).size());
        workflowVariables = getWorkflowVariables(route.getDocument());
        assertEquals(false, workflowVariables.get("active"));
        assertEquals((long) 2, workflowVariables.get(WF_ACTIVE_SECONDS_COUNT));
        assertAuditActiveAndResumeCount(route, 2, 3);
        assertAllActiveWorkflows(anySession, 0);
        assertAllActiveTasks(anySession, 0);
    }

    @Test
    public void shouldLaunchWorkflowActivateResumeAsAdministrator() throws OperationException {
        try (CloseableCoreSession admin = openSessionAsUser(ADMIN.getUserName())) {
            shouldLaunchWorkflowActivateResumeAs(admin);
        }
    }

    @Test
    public void shouldLaunchWorkflowActivateResumeAsMember() throws OperationException {
        try (CloseableCoreSession member = openSessionAsUser(MEMBER_X.getUserName())) {
            shouldLaunchWorkflowActivateResumeAs(member);
        }
    }

    protected DocumentRoute launchWorkflow(CoreSession coreSession, DocumentModel doc, String wfName) {
        assertEquals(0, getDocumentRelatedWorkflows(doc, coreSession).size());

        String route = startWorkflowAndGetRouteId(documentRoutingService, coreSession, doc, wfName);
        waitForAsyncExec(1);

        assertEquals(1, getDocumentRelatedWorkflows(doc, coreSession).size());
        return getDocumentRelatedWorkflows(doc, coreSession).get(0);
    }

    protected void resumeWorkflow(CoreSession coreSession, DocumentModel wfDoc) throws OperationException {
        OperationContext ctx = new OperationContext(session);
        ctx.setInput(wfDoc);
        automationService.run(ctx, WorkflowResume.ID);
        waitForAsyncExec(2);
    }

    protected void activateWorkflow(CoreSession coreSession, DocumentModel wfDoc) throws OperationException {
        OperationContext ctx = new OperationContext(session);
        ctx.setInput(wfDoc);
        automationService.run(ctx, WorkflowActivate.ID);
        waitForAsyncExec(1);
    }

    protected void assertAuditActiveAndResumeCount(DocumentRoute wfDoc, int activeCount, int resumeCount) {
        List<String> activateAudit = TestUtils.getLogEntries(wfDoc.getDocument(), "activate");
        List<String> resumeAudit = TestUtils.getLogEntries(wfDoc.getDocument(), "resume");

        assertEquals(activeCount, activateAudit.size());
        assertEquals(resumeCount, resumeAudit.size());

        if (activateAudit.size() > 0) {
            assertTrue(activateAudit.toString().contains("activate workflow"));
        }

        if (resumeAudit.size() > 0) {
            assertTrue(resumeAudit.toString().contains("resume workflow"));
        }

    }

    protected void assertAllActiveWorkflows(CoreSession userSession, int count) throws OperationException {
        OperationContext ctx = new OperationContext(userSession);
        DocumentModelList list = (DocumentModelList) automationService.run(ctx, WorkflowGetAllActive.ID);
        assertEquals(count, list.size());
    }

    protected void assertAllActiveTasks(CoreSession userSession, int count) throws OperationException {
        OperationContext ctx = new OperationContext(userSession);
        DocumentModelList list = (DocumentModelList) automationService.run(ctx, WorkflowTaskGetAllActive.ID);
        assertEquals(count, list.size());
    }

}
