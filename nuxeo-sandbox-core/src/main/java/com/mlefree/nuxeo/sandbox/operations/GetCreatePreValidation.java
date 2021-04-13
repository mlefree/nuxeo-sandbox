package com.mlefree.nuxeo.sandbox.operations;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

@Operation(id = GetCreatePreValidation.ID, category = Constants.CAT_DOCUMENT, label = "Pre validate document", description = "Pre validate document.")
public class GetCreatePreValidation {

    public static final String ID = "Document.GetCreatePreValidation";

    @Context
    protected CoreSession session;

    @OperationMethod
    public String run(DocumentModel documentModel) throws NuxeoException {
        return "todo";
    }

}
