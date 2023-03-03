package com.mlefree.nuxeo.sandbox.utils;

import java.util.Collections;
import java.util.Date;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.audit.api.AuditLogger;
import org.nuxeo.ecm.platform.audit.api.LogEntry;
import org.nuxeo.runtime.api.Framework;

public class AuditLogUtils {

    public static void addAuditLog(DocumentModel doc, String username, String eventId, String eventCategory, String comment) {
        AuditLogger auditLogger = Framework.getService(AuditLogger.class);
        LogEntry logEntry = auditLogger.newLogEntry();

        logEntry.setEventId(eventId);
        logEntry.setCategory(eventCategory);
        logEntry.setEventDate(new Date());
        if (doc != null) {
            logEntry.setDocUUID(doc.getId());
            logEntry.setDocPath(doc.getPathAsString());
            logEntry.setDocType(doc.getType());
            logEntry.setDocLifeCycle(doc.getCurrentLifeCycleState());
            logEntry.setRepositoryId(doc.getRepositoryName());
        }
        logEntry.setEventDate(new Date());
        logEntry.setPrincipalName(username);
        logEntry.setComment(comment);

        auditLogger.addLogEntries(Collections.singletonList(logEntry));
    }

}
