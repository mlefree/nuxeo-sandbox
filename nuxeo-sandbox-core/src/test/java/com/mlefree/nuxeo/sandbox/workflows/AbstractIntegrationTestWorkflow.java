package com.mlefree.nuxeo.sandbox.workflows;

import static com.mlefree.nuxeo.sandbox.features.MleFeature.openSessionAsUser;
import static com.mlefree.nuxeo.sandbox.features.MleFeature.waitForAsyncExec;
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

import com.mlefree.nuxeo.sandbox.operations.WorkflowActivate;
import com.mlefree.nuxeo.sandbox.operations.WorkflowResume;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.routing.api.DocumentRoute;
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

    protected final static  String ACTIVE_NOT_COMPATIBLE_WF_01 = "wfnoactivation";

    @Inject
    protected CoreSession session;

    @Inject
    protected DocumentRoutingService documentRoutingService;

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

}
