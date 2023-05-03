package com.mlefree.nuxeo.sandbox.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class })
@Deploy("com.mlefree.nuxeo.sandbox.nuxeo-sandbox-core")
@Deploy("com.mlefree.nuxeo.sandbox.nuxeo-sandbox-core-test:test-qr-contrib.xml")
public class TestQRCode {

    @Inject
    CoreSession session;
    @Inject
    protected QRCode qrcode;

    @Test
    public void testService() {
        assertNotNull(qrcode);
    }

    @Test
    public void shouldGetQRCode() {
        DocumentModel file = session.createDocumentModel("/","myDoc", "File");
        file.setPropertyValue("dc:title", "my title");
        file.setPropertyValue("dc:description", "my description");
        file = session.createDocument(file);

        assertEquals("QR_my title_my description", qrcode.getQRCode(file));
    }

}
