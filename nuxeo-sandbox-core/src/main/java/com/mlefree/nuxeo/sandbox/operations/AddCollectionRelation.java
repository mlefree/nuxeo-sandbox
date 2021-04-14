package com.mlefree.nuxeo.sandbox.operations;

import static java.lang.Boolean.TRUE;
import static org.nuxeo.ecm.collections.api.CollectionConstants.DISABLE_NOTIFICATION_SERVICE;
import static org.nuxeo.ecm.collections.api.CollectionConstants.DOCUMENT_COLLECTION_IDS_PROPERTY_NAME;
import static org.nuxeo.ecm.core.api.security.SecurityConstants.ADMINISTRATOR;
import static org.nuxeo.ecm.core.api.versioning.VersioningService.DISABLE_AUTO_CHECKOUT;
import static org.nuxeo.ecm.core.bulk.action.SetPropertiesAction.PARAM_DISABLE_AUDIT;
import static org.nuxeo.ecm.core.schema.FacetNames.HIDDEN_IN_NAVIGATION;
import static org.nuxeo.ecm.platform.dublincore.listener.DublinCoreListener.DISABLE_DUBLINCORE_LISTENER;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.collections.api.CollectionConstants;
import org.nuxeo.ecm.collections.api.CollectionManager;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.event.CoreEventConstants;
import org.nuxeo.ecm.core.api.pathsegment.PathSegmentService;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

@Operation(id = AddCollectionRelation.ID, category = Constants.CAT_DOCUMENT, label = "AddCollectionRelation document", description = "AddCollectionRelation document.")
public class AddCollectionRelation {

    public static final String ID = "Document.AddCollectionRelation";

    @Context
    protected CoreSession session;

    @Context
    protected UserManager userManager;

    @Param(name = "target")
    protected DocumentModel target;

    public static final String SELECT_HIDDEN_COLLECTIONS = "SELECT * FROM Document WHERE ecm:mixinType = 'Collection' AND ecm:mixinType = 'HiddenInNavigation'";

    @OperationMethod
    public void run(DocumentModel source) throws NuxeoException {

        // inspired by Document.AddToCollection and Collection.Create...

        // TODO make search more robust by searching on fields, not on title
        String collectionTitle = "_hiddenCollection_" + source.getId() + "_" + target.getType();

        try (CloseableCoreSession adminSession = CoreInstance.openCoreSession(session.getRepositoryName(),
                userManager.getPrincipal(ADMINISTRATOR))) {

            final CollectionManager collectionManager = Framework.getService(CollectionManager.class);

            String query = SELECT_HIDDEN_COLLECTIONS + " AND dc:title = '" + collectionTitle + "'";
            DocumentModelList results = adminSession.query(query);
            DocumentModel collection;
            if (results.size() == 1) {
                collection = results.get(0);
            } else {
                DocumentModel defaultCollections = collectionManager.getUserDefaultCollections(adminSession);
                PathSegmentService pss = Framework.getService(PathSegmentService.class);
                Map<String, Object> options = new HashMap<>();
                options.put(CoreEventConstants.PARENT_PATH, defaultCollections.getPath().toString());
                options.put(CoreEventConstants.DESTINATION_NAME, pss.generatePathSegment(collectionTitle));

                // TODO need to extend Collection type with more clear relation fields
                collection = adminSession.createDocumentModel(CollectionConstants.COLLECTION_TYPE, options);
                collection.setPropertyValue("dc:title", collectionTitle);
                collection.setPropertyValue("dc:description", target.getType());
                collection.setPropertyValue("dc:source", source.getId());
                collection.addFacet(HIDDEN_IN_NAVIGATION);
                collection = adminSession.createDocument(collection);
                collection = adminSession.saveDocument(collection);
            }

            collectionManager.addToCollection(collection, target, adminSession);
            collection = adminSession.saveDocument(collection);

            // TODO get from target document collectionIds and clean it
            List<String> collectionIds = (List<String>) target.getPropertyValue(DOCUMENT_COLLECTION_IDS_PROPERTY_NAME);
            List<String> collectionsIdsClean = new ArrayList<>();
            target.setPropertyValue(DOCUMENT_COLLECTION_IDS_PROPERTY_NAME, (Serializable) collectionsIdsClean);
            target.putContextData(DISABLE_AUTO_CHECKOUT, TRUE);
            target.putContextData(DISABLE_DUBLINCORE_LISTENER, TRUE);
            target.putContextData(DISABLE_NOTIFICATION_SERVICE, TRUE);
            target.putContextData(PARAM_DISABLE_AUDIT, TRUE);
            adminSession.saveDocument(target);
        }
    }

}
