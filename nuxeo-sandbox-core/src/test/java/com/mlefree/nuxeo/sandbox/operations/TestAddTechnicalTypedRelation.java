package com.mlefree.nuxeo.sandbox.operations;

import static com.mlefree.nuxeo.sandbox.MleFeature.openSessionAsUser;
import static com.mlefree.nuxeo.sandbox.MleFeature.waitForAsyncExec;
import static com.mlefree.nuxeo.sandbox.studio.StudioConstant.TECHNICALRELATIONTYPED_SCHEMA_SOURCE_PROPERTY;
import static com.mlefree.nuxeo.sandbox.studio.StudioConstant.TECHNICALRELATIONTYPED_SCHEMA_TARGETS_PROPERTY;
import static com.mlefree.nuxeo.sandbox.studio.StudioConstant.TECHNICALRELATIONTYPED_SCHEMA_TYPE_PROPERTY;
import static com.mlefree.nuxeo.sandbox.studio.StudioConstant.TECHNICAL_TYPED_RELATION_DOC_TYPE;
import static org.junit.Assert.assertEquals;
import static org.nuxeo.ecm.core.api.security.SecurityConstants.ADMINISTRATOR;

import java.util.Arrays;
import java.util.HashMap;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.mlefree.nuxeo.sandbox.MleFeature;

@RunWith(FeaturesRunner.class)
@Features({ MleFeature.class, AutomationFeature.class })
public class TestAddTechnicalTypedRelation {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    public static final String SELECT_RELATIONS = "SELECT * FROM Document WHERE ecm:primaryType = '"
            + TECHNICAL_TYPED_RELATION_DOC_TYPE + "'";

    @Test
    public void shouldAddRelation() throws OperationException {

        DocumentModel folder1 = session.createDocumentModel("/", "folder1", "Folder");
        folder1.setPropertyValue("dc:title", "folder1");
        folder1.setPropertyValue("dc:description", "test folder");
        folder1 = session.createDocument(folder1);
        session.saveDocument(folder1);

        DocumentModel folder2 = session.createDocumentModel("/", "folder2", "Folder");
        folder2.setPropertyValue("dc:title", "folder2");
        folder2.setPropertyValue("dc:description", "test folder");
        folder2 = session.createDocument(folder2);
        session.saveDocument(folder2);

        OperationContext ctx = new OperationContext(session);
        ctx.setInput(folder1);
        HashMap<String, String> params = new HashMap<>();
        params.put("target", folder2.getId());
        automationService.run(ctx, AddTechnicalTypedRelation.ID, params);
        waitForAsyncExec();

        try (CloseableCoreSession adminSession = openSessionAsUser(ADMINISTRATOR)) {
            DocumentModelList results = adminSession.query(SELECT_RELATIONS);
            assertEquals(1, results.size());
            DocumentModel relation = results.get(0);

            assertEquals(folder1.getId(), relation.getPropertyValue(TECHNICALRELATIONTYPED_SCHEMA_SOURCE_PROPERTY));
            assertEquals(folder2.getType(), relation.getPropertyValue(TECHNICALRELATIONTYPED_SCHEMA_TYPE_PROPERTY));
            String[] targets = (String[]) relation.getPropertyValue(TECHNICALRELATIONTYPED_SCHEMA_TARGETS_PROPERTY);
            assertEquals(1, targets.length);
            assertEquals("[" + folder2.getId() + "]", Arrays.toString(targets));
        }
    }

}
