package com.mlefree.nuxeo.sandbox.workflows;

import static com.mlefree.nuxeo.sandbox.MleRepositoryInit.FILE_A_PATH;
import static com.mlefree.nuxeo.sandbox.features.MleFeature.openSessionAsUser;
import static com.mlefree.nuxeo.sandbox.features.StudioWorkflowFeature.getWorkflowVariables;
import static com.mlefree.nuxeo.sandbox.features.StudioWorkflowFeature.assertYourPublishWorkflowIsFinished;
import static com.mlefree.nuxeo.sandbox.features.StudioWorkflowFeature.getAllRelatedWorkflows;
import static com.mlefree.nuxeo.sandbox.features.StudioWorkflowFeature.goToTheStepInWorkflow;
import static com.mlefree.nuxeo.sandbox.features.StudioWorkflowFeature.startWorkflowAndGetRouteId;
import static com.mlefree.nuxeo.sandbox.utils.UsersConfiguration.ADMIN;
import static com.mlefree.nuxeo.sandbox.utils.WorkflowUtils.getWorkflowVariables;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.mlefree.nuxeo.sandbox.utils.TestUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.routing.api.DocumentRoute;
import org.nuxeo.runtime.test.runner.TransactionalFeature;

public class TestWorkflowPublish extends AbstractIntegrationTestWorkflow {

    @Inject
    TransactionalFeature txFeature;

    @Test
    public void shouldLaunchWorkflowAsAdministrator() {
        try (CloseableCoreSession adminSession = openSessionAsUser(ADMIN.getUserName())) {
            DocumentModel docX = adminSession.getDocument(new PathRef(FILE_A_PATH));
            assertEquals(0, getAllRelatedWorkflows(docX).size());

            String route = startWorkflowAndGetRouteId(documentRoutingService, adminSession, docX, "wftest");

            assertEquals(1, getAllRelatedWorkflows(docX).size());
            DocumentRoute dr = getAllRelatedWorkflows(docX).get(0);
            Map<String, Serializable> workflowVariables = getWorkflowVariables(documentRoutingService, docX, adminSession);
            assertEquals(true, workflowVariables.get("active"));

            goToTheStepInWorkflow(docX, adminSession, "validate");

            assertYourPublishWorkflowIsFinished(route, docX, adminSession);

            workflowVariables = getWorkflowVariables(dr.getDocument());
            assertEquals(false, workflowVariables.get("active"));

            assertEquals(0, getAllRelatedWorkflows(docX).size());
        }

    }

    @Test
    public void shouldResumeWorkflowAsAdministrator() {
        try (CloseableCoreSession adminSession = openSessionAsUser(ADMIN.getUserName())) {
            DocumentModel docX = adminSession.getDocument(new PathRef(FILE_A_PATH));
            assertEquals(0, getAllRelatedWorkflows(docX).size());

            String route = startWorkflowAndGetRouteId(documentRoutingService, adminSession, docX, "wftest");

            assertEquals(1, getAllRelatedWorkflows(docX).size());
            DocumentRoute dr = getAllRelatedWorkflows(docX).get(0);
            Map<String, Serializable> workflowVariables = getWorkflowVariables(documentRoutingService, docX, adminSession);
            assertEquals(true, workflowVariables.get("active"));

            goToTheStepInWorkflow(docX, adminSession, "resume");

            workflowVariables = getWorkflowVariables(dr.getDocument());
            assertEquals(false, workflowVariables.get("active"));

            assertEquals(1, getAllRelatedWorkflows(docX).size());
        }

    }

    @Test
    @Ignore
    public void shouldAuditLogWithdraws() {
        List<String> goBackDone = new ArrayList<>();

        startWFAsFactory(goBackDone);
        // continuePublishWFAsFactoryToCompany(goBackDone);

        txFeature.nextTransaction();

        DocumentModel docX = session.getDocument(new PathRef(FILE_A_PATH));
        List<String> workflowAuditMessages = TestUtils.getLogEntries(session, docX, "withdraw");

        assertNotNull(workflowAuditMessages);
        assertEquals(3, workflowAuditMessages.size());
        assertTrue(
                workflowAuditMessages.contains("User factoryXuser withdrawn document during PublishWorkflow workflow"));
        assertTrue(
                workflowAuditMessages.contains("User companyAuser withdrawn document during PublishWorkflow workflow"));
        assertTrue(workflowAuditMessages.contains("User PsvdA withdrawn document during PublishWorkflow workflow"));
    }

}
