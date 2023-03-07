package com.mlefree.nuxeo.sandbox.operations;

import static com.mlefree.nuxeo.sandbox.utils.WorkflowUtils.getAllWorkflows;
import static junit.framework.TestCase.assertEquals;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.routing.api.DocumentRoute;
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
        List<DocumentRoute> wfs = getAllWorkflows(session);
        assertEquals(1, wfs.size());

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(wfs.get(0).getDocument());
        automationService.run(ctx, WorkflowResume.ID);
        transactionalFeature.nextTransaction();
    }

}
