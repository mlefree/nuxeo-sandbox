package com.mlefree.nuxeo.sandbox.utils;

import static org.nuxeo.ecm.core.query.sql.model.Predicates.eq;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.query.sql.model.QueryBuilder;
import org.nuxeo.ecm.platform.audit.api.AuditQueryBuilder;
import org.nuxeo.ecm.platform.audit.api.AuditReader;
import org.nuxeo.ecm.platform.audit.api.LogEntry;
import org.nuxeo.runtime.api.Framework;

public class TestUtils {

    public static DocumentModel createDummyFileDocument(CoreSession session, String path, String title) {
        DocumentModel doc = session.createDocumentModel(path, title, "File");
        doc.setPropertyValue("dc:title", title);
        return session.createDocument(doc);
    }

    public static List<String> getLogEntries(DocumentModel doc, String eventIDToRetrieve) {
        AuditReader reader = Framework.getService(AuditReader.class);
        QueryBuilder builder = new AuditQueryBuilder().predicate(eq("eventId", eventIDToRetrieve));
        if (doc != null) {
            builder.and(eq("docUUID", doc.getId()));
        }

        List<String> entries = new ArrayList<>();
        for (LogEntry entry : reader.queryLogs(builder)) {
            entries.add(entry.getComment());
        }

        return entries;
    }
}
