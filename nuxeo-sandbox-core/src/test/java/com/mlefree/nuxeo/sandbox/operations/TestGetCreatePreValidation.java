package com.mlefree.nuxeo.sandbox.operations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlefree.nuxeo.sandbox.MleFeature;
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

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(FeaturesRunner.class)
@Features({ MleFeature.class, AutomationFeature.class })
public class TestGetCreatePreValidation {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String CHECK_OK_DEFAULT_ANSWER = "{\"ok\":true,\"status\":200,\"message\":\"\", \"errors\":[], \"restrictedGroups\":[]}";

    @Test
    public void verifyOutputOfPreValidationIsWhatFrontEndIsExpecting() throws OperationException {
        DocumentModel categoryWithoutPrimaryApplicativeReferences = session.createDocumentModel("/", "File",
                "File");
        categoryWithoutPrimaryApplicativeReferences.setPropertyValue("category:category", "PEV_PRES");
        categoryWithoutPrimaryApplicativeReferences.setPropertyValue("category:retentionDurationInYears", 20);
        categoryWithoutPrimaryApplicativeReferences.setPropertyValue("confidentiality:confidentialityLevel", "C2");
        categoryWithoutPrimaryApplicativeReferences = session.createDocument(categoryWithoutPrimaryApplicativeReferences);

        DocumentModel docWithCategory = session.createDocumentModel("/", "docWithCategory", "QualityDocumentationDocument");
        docWithCategory.setPropertyValue("reglementary:documentCategory", categoryWithoutPrimaryApplicativeReferences.getId());

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(docWithCategory);
        String result = (String) automationService.run(ctx, GetCreatePreValidation.ID);

        assertNotNull(result);
        assertEquals(CHECK_OK_DEFAULT_ANSWER, result);
    }

}
