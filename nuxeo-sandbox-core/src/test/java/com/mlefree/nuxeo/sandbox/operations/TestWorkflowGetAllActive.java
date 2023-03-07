package com.mlefree.nuxeo.sandbox.operations;

import static com.mlefree.nuxeo.sandbox.utils.WorkflowUtils.getAllWorkflows;
import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.routing.api.DocumentRoute;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.mlefree.nuxeo.sandbox.features.MleFeature;
import com.mlefree.nuxeo.sandbox.features.StudioWorkflowIntegrationTestFeature;

import java.util.List;

@RunWith(FeaturesRunner.class)
@Features({ MleFeature.class, StudioWorkflowIntegrationTestFeature.class })
public class TestWorkflowGetAllActive {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Test
    public void shouldGetAll() throws OperationException {
        List<DocumentRoute> wfs = getAllWorkflows(session);
        TestCase.assertEquals(1, wfs.size());

        OperationContext ctx = new OperationContext(session);
        DocumentModelList activeWfs = (DocumentModelList) automationService.run(ctx, WorkflowGetAllActive.ID);

        assertEquals(0, activeWfs.size());

    }

}
