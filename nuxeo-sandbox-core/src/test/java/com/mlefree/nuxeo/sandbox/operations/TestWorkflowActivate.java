package com.mlefree.nuxeo.sandbox.operations;

import static com.mlefree.nuxeo.sandbox.utils.UserManagementUtils.openSessionAsSystem;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.routing.api.DocumentRoutingConstants;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.TransactionalFeature;

import com.mlefree.nuxeo.sandbox.features.MleFeature;
import com.mlefree.nuxeo.sandbox.features.StudioWorkflowIntegrationTestFeature;

@RunWith(FeaturesRunner.class)
@Features({ MleFeature.class, StudioWorkflowIntegrationTestFeature.class })
public class TestWorkflowActivate {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Inject
    protected TransactionalFeature transactionalFeature;

    @Test
    public void shouldActivate() throws OperationException {
        DocumentModel wf1 = session.createDocumentModel("/", "wf1",
                DocumentRoutingConstants.DOCUMENT_ROUTE_DOCUMENT_TYPE);
        wf1.setPropertyValue("dc:title", "wf1");
        wf1.setPropertyValue("active", false);
        wf1 = session.createDocument(wf1);
        session.saveDocument(wf1);

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(wf1);
        automationService.run(ctx, WorkflowActivate.ID);
        transactionalFeature.nextTransaction();

    }

}
