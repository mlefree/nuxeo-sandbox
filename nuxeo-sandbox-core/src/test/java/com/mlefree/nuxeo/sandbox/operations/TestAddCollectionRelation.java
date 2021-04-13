package com.mlefree.nuxeo.sandbox.operations;

import static com.mlefree.nuxeo.sandbox.MleFeature.openSessionAsUser;
import static org.junit.Assert.assertEquals;
import static org.nuxeo.ecm.collections.api.CollectionConstants.DOCUMENT_COLLECTION_IDS_PROPERTY_NAME;
import static org.nuxeo.ecm.core.api.security.SecurityConstants.ADMINISTRATOR;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.TransactionalFeature;

import com.mlefree.nuxeo.sandbox.MleFeature;

@RunWith(FeaturesRunner.class)
@Features({ MleFeature.class, AutomationFeature.class })
@Deploy({ "org.nuxeo.ecm.default.config" })
@Deploy("org.nuxeo.ecm.platform.usermanager")
public class TestAddCollectionRelation {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Context
    protected UserManager userManager;

    @Inject
    protected TransactionalFeature transactionalFeature;

    public static final String SELECT_HIDDEN_COLLECTIONS = "SELECT * FROM Document WHERE ecm:mixinType = 'Collection' AND ecm:mixinType = 'HiddenInNavigation'";

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
        automationService.run(ctx, AddCollectionRelation.ID, params);
        transactionalFeature.nextTransaction();

        folder1 = session.getDocument(folder1.getRef());
        folder2 = session.getDocument(folder2.getRef());
        assertEquals(0, ((List<String>) folder2.getPropertyValue(DOCUMENT_COLLECTION_IDS_PROPERTY_NAME)).size());

        try (CloseableCoreSession adminSession = openSessionAsUser(ADMINISTRATOR)) {
            DocumentModelList results = adminSession.query(SELECT_HIDDEN_COLLECTIONS);
            assertEquals(1, results.size());

            assertEquals(folder2.getType(), results.get(0).getPropertyValue("dc:description"));
            assertEquals(folder2.getId(), results.get(0).getPropertyValue("dc:source"));
        }
    }

}
