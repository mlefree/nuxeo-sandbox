package com.mlefree.nuxeo.sandbox.workflows;

import static com.mlefree.nuxeo.sandbox.features.MleFeature.openSessionAsUser;
import static com.mlefree.nuxeo.sandbox.features.StudioWorkflowFeature.followWorkflowTransition;
import static com.mlefree.nuxeo.sandbox.features.StudioWorkflowFeature.getRelatedWithdrawableWorkflows;
import static com.mlefree.nuxeo.sandbox.features.StudioWorkflowFeature.getWorkflowVariables;
import static com.mlefree.nuxeo.sandbox.features.StudioWorkflowFeature.startWorkflowAndGetRouteId;
import static com.mlefree.nuxeo.sandbox.utils.UsersConfiguration.ADMIN;
import static com.mlefree.nuxeo.sandbox.utils.UsersConfiguration.MEMBER_X;
import static com.mlefree.nuxeo.sandbox.utils.WorkflowUtils.getDocumentRelatedWorkflows;
import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.routing.api.DocumentRoutingService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.TransactionalFeature;

import com.mlefree.nuxeo.sandbox.MleRepositoryInit;
import com.mlefree.nuxeo.sandbox.features.MleFeature;
import com.mlefree.nuxeo.sandbox.features.StudioWorkflowIntegrationTestFeature;

@RunWith(FeaturesRunner.class)
@Features({ MleFeature.class, StudioWorkflowIntegrationTestFeature.class })
public abstract class AbstractIntegrationTestWorkflow {

    protected final static String ACTIVE_COMPATIBLE_WF_01 = "wftest";

    @Inject
    protected CoreSession session;

    @Inject
    protected DocumentRoutingService documentRoutingService;

    @Inject
    TransactionalFeature txFeature;

    @Inject
    protected AutomationService automationService;

    @Before
    public void prepare() {
        // MleRepositoryInit.createDocuments(session);
    }

    @After
    public void reset() {
        documentRoutingService.invalidateRouteModelsCache();
        documentRoutingService.cleanupDoneAndCanceledRouteInstances(session.getRepositoryName(), 0);
    }

    protected void startWFAsFactory(List<String> goBackDone) {
        try (CloseableCoreSession memberSession = openSessionAsUser(MEMBER_X.getUserName())) {
            DocumentModel file = memberSession.getDocument(new PathRef(MleRepositoryInit.FILE_A_PATH));
            startWF(memberSession, goBackDone, file);
        }
    }

    protected void startWFAsFactoryAdministrator(List<String> goBackDone) {
        try (CloseableCoreSession adminSession = openSessionAsUser(ADMIN.getUserName())) {
            DocumentModel file = adminSession.getDocument(new PathRef(MleRepositoryInit.FILE_A_PATH));
            startWF(adminSession, goBackDone, file);
        }
    }

    private void startWF(CloseableCoreSession userSession, List<String> goBackDone, DocumentModel doc) {
        assertEquals(0, getDocumentRelatedWorkflows(doc, userSession).size());
        assertEquals(0, getRelatedWithdrawableWorkflows(userSession, doc).size());

        startWorkflowAndGetRouteId(documentRoutingService, userSession, doc, "wftest");

        Map<String, Serializable> workflowVariables = getWorkflowVariables(documentRoutingService, doc, userSession);

        assertEquals(true, workflowVariables.get("active"));
        assertEquals(1, getDocumentRelatedWorkflows(doc, userSession).size());
        assertEquals(0, getRelatedWithdrawableWorkflows(userSession, doc).size());
    }

    protected void abandonWorkflowAsFactory(CloseableCoreSession userSession, List<String> goBackDone,
            DocumentModel doc) {
        try (CloseableCoreSession faSession = openSessionAsUser(MEMBER_X.getUserName())) {
            DocumentModel docX = faSession.getDocument(new PathRef(MleRepositoryInit.FILE_A_PATH));
            assertEquals(1, getDocumentRelatedWorkflows(doc, userSession).size());
            followWorkflowTransition(docX, faSession, "abandon");
            goBackDone.add("abandon");
            assertEquals(0, getDocumentRelatedWorkflows(doc, userSession).size());
        }
    }

    protected void executePublishWorkflowOnDocument(String documentPath) {
        try (CloseableCoreSession sessionAdmin = openSessionAsUser(ADMIN.getUserName())) {
            DocumentModel docX = sessionAdmin.getDocument(new PathRef(documentPath));

            docX.followTransition("to_CompanyApproving");
            sessionAdmin.saveDocument(docX);
        }
    }

}
