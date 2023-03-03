package com.mlefree.nuxeo.sandbox.workflows;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.mlefree.nuxeo.sandbox.MleRepositoryInit;
import com.mlefree.nuxeo.sandbox.features.MleFeature;
import com.mlefree.nuxeo.sandbox.features.StudioWorkflowIntegrationTestFeature;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.routing.api.DocumentRoutingService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import static com.mlefree.nuxeo.sandbox.features.MleFeature.*;
import static com.mlefree.nuxeo.sandbox.features.StudioWorkflowFeature.*;
import static com.mlefree.nuxeo.sandbox.utils.UsersConfiguration.*;
import static org.junit.Assert.assertEquals;


@RunWith(FeaturesRunner.class)
@Features({MleFeature.class, StudioWorkflowIntegrationTestFeature.class })
public abstract class AbstractIntegrationTestWorkflow {

    @Inject
    protected CoreSession session;

    @Inject
    protected DocumentRoutingService documentRoutingService;

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
        assertEquals(0, getAllRelatedWorkflows(doc).size());
        assertEquals(0, getRelatedWithdrawableWorkflows(userSession, doc).size());

        startWorkflowAndGetRouteId(documentRoutingService, userSession, doc, "wftest");

        Map<String, Serializable> workflowVariables = getWorkflowVariables(documentRoutingService, doc, userSession);

        assertEquals(true, workflowVariables.get("active"));
        assertEquals(1, getAllRelatedWorkflows(doc).size());
        assertEquals(0, getRelatedWithdrawableWorkflows(userSession, doc).size());
    }

    protected void abandonWorkflowAsFactory(List<String> goBackDone) {
        try (CloseableCoreSession faSession = openSessionAsUser(MEMBER_X.getUserName())) {
            DocumentModel docX = faSession.getDocument(new PathRef(MleRepositoryInit.FILE_A_PATH));
            assertEquals(1, getAllRelatedWorkflows(docX).size());
            goToTheStepInWorkflow(docX, faSession, "abandon");
            goBackDone.add("abandon");
            assertEquals(0, getAllRelatedWorkflows(docX).size());
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
