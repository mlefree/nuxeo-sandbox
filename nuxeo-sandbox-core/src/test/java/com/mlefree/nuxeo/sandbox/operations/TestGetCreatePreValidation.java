package com.mlefree.nuxeo.sandbox.operations;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.mlefree.nuxeo.sandbox.features.MleFeature;

@RunWith(FeaturesRunner.class)
@Features({ MleFeature.class, AutomationFeature.class })
public class TestGetCreatePreValidation {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Test
    public void shouldValidate() throws OperationException {
        DocumentModel file = session.createDocumentModel("/", "File", "File");

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(file);
        String result = (String) automationService.run(ctx, GetCreatePreValidation.ID);

        assertEquals("todo", result);
    }

}
