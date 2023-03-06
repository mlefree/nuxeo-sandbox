package com.mlefree.nuxeo.sandbox.operations;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.TransactionalFeature;

import com.mlefree.nuxeo.sandbox.features.MleFeature;
import com.mlefree.nuxeo.sandbox.features.StudioWorkflowIntegrationTestFeature;

@RunWith(FeaturesRunner.class)
@Features({ MleFeature.class, StudioWorkflowIntegrationTestFeature.class })
public class TestWorkflowGetAllActive {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Inject
    protected TransactionalFeature transactionalFeature;

    @Test
    public void shouldGetAll() throws OperationException {

        OperationContext ctx = new OperationContext(session);
        automationService.run(ctx, WorkflowGetAllActive.ID);
        transactionalFeature.nextTransaction();

    }

}
