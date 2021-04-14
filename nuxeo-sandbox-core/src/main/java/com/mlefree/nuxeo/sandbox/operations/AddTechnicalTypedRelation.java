package com.mlefree.nuxeo.sandbox.operations;

import static com.mlefree.nuxeo.sandbox.studio.StudioConstant.TECHNICALRELATIONTYPED_SCHEMA_SOURCE_PROPERTY;
import static com.mlefree.nuxeo.sandbox.studio.StudioConstant.TECHNICALRELATIONTYPED_SCHEMA_TARGETS_PROPERTY;
import static com.mlefree.nuxeo.sandbox.studio.StudioConstant.TECHNICALRELATIONTYPED_SCHEMA_TYPE_PROPERTY;
import static com.mlefree.nuxeo.sandbox.studio.StudioConstant.TECHNICAL_TYPED_RELATION_DOC_TYPE;
import static org.nuxeo.ecm.core.api.security.SecurityConstants.ADMINISTRATOR;
import static org.nuxeo.ecm.core.schema.FacetNames.HIDDEN_IN_NAVIGATION;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.ecm.platform.usermanager.UserManager;

@Operation(id = AddTechnicalTypedRelation.ID, category = Constants.CAT_DOCUMENT, label = "AddTechnicalTypedRelation", description = "AddTechnicalTypedRelation")
public class AddTechnicalTypedRelation {

    public static final String ID = "Document.AddTechnicalTypedRelation";

    public static final Log log = LogFactory.getLog(AddTechnicalTypedRelation.class);

    @Context
    protected CoreSession session;

    @Context
    protected UserManager userManager;

    @Param(name = "target")
    protected DocumentModel target;

    public static final String SELECT_RELATIONS = "SELECT * FROM Document WHERE ecm:primaryType = '"
            + TECHNICAL_TYPED_RELATION_DOC_TYPE + "'";

    @OperationMethod
    public void run(DocumentModel source) throws NuxeoException {

        String relationTitle = "_hiddenRelation_" + source.getId() + "_" + target.getType();

        try (CloseableCoreSession adminSession = CoreInstance.openCoreSession(session.getRepositoryName(),
                userManager.getPrincipal(ADMINISTRATOR))) {

            String query = SELECT_RELATIONS + " AND " + TECHNICALRELATIONTYPED_SCHEMA_SOURCE_PROPERTY + " = '"
                    + source.getId() + "' AND " + TECHNICALRELATIONTYPED_SCHEMA_TYPE_PROPERTY + " = '"
                    + target.getType() + "'";
            DocumentModelList results = adminSession.query(query);
            DocumentModel relation;

            // Create if not exist
            if (results.size() == 1) {
                relation = results.get(0);
            } else {
                relation = adminSession.createDocumentModel("/", relationTitle, TECHNICAL_TYPED_RELATION_DOC_TYPE);
                relation.setPropertyValue("dc:title", relationTitle);
                relation.setPropertyValue(TECHNICALRELATIONTYPED_SCHEMA_SOURCE_PROPERTY, source.getId());
                relation.setPropertyValue(TECHNICALRELATIONTYPED_SCHEMA_TYPE_PROPERTY, target.getType());
                relation = adminSession.createDocument(relation);
                relation.addFacet(HIDDEN_IN_NAVIGATION);
            }

            // Update set of targets
            Property prop = relation.getProperty(TECHNICALRELATIONTYPED_SCHEMA_TARGETS_PROPERTY);
            String[] propValue = (String[]) Optional.ofNullable(prop.getValue()).orElse(new String[] {});
            Set<String> targets = new HashSet<>(Arrays.asList(propValue));
            targets.add(target.getId());
            relation.setPropertyValue(TECHNICALRELATIONTYPED_SCHEMA_TARGETS_PROPERTY, (Serializable) targets);

            relation = adminSession.saveDocument(relation);
            String relSourceId = (String) relation.getPropertyValue(TECHNICALRELATIONTYPED_SCHEMA_SOURCE_PROPERTY);
            log.info("Typed Relation added between " + relSourceId + " and " + targets);
        }
    }

}
