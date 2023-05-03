package com.mlefree.nuxeo.sandbox.services;

import org.nuxeo.ecm.core.api.DocumentModel;

public interface QRCode {
    /** Add some methods here. **/

    public String getQRCode(DocumentModel doc);
}
