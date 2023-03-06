package com.mlefree.nuxeo.sandbox.operations;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.TransactionalFeature;

import com.mlefree.nuxeo.sandbox.features.MleFeature;
import com.mlefree.nuxeo.sandbox.features.StudioWorkflowIntegrationTestFeature;

@RunWith(FeaturesRunner.class)
@Features({ MleFeature.class, StudioWorkflowIntegrationTestFeature.class })
public class TestWorkflowResume {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Inject
    protected TransactionalFeature transactionalFeature;

    @Test
    public void shouldResume() throws OperationException {

        DocumentModel folder1 = session.createDocumentModel("/", "folder1", "Folder");
        folder1.setPropertyValue("dc:title", "folder1");
        folder1.setPropertyValue("dc:description", "test folder");
        folder1 = session.createDocument(folder1);
        session.saveDocument(folder1);

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(folder1);
        automationService.run(ctx, WorkflowResume.ID);
        transactionalFeature.nextTransaction();

    }

}
