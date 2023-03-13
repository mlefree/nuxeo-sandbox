package com.mlefree.nuxeo.sandbox.enrichers;

import static org.nuxeo.ecm.core.io.registry.reflect.Instantiations.SINGLETON;
import static org.nuxeo.ecm.core.io.registry.reflect.Priorities.REFERENCE;

import java.io.IOException;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.io.marshallers.json.enrichers.AbstractJsonEnricher;
import org.nuxeo.ecm.core.io.registry.context.RenderingContext.SessionWrapper;
import org.nuxeo.ecm.core.io.registry.reflect.Setup;
import org.nuxeo.ecm.platform.task.Task;

import com.fasterxml.jackson.core.JsonGenerator;

@Setup(mode = SINGLETON, priority = REFERENCE)
public class TaskJsonEnricher extends AbstractJsonEnricher<DocumentModel> {

    public static final String NAME = "taskExtended";

    public TaskJsonEnricher() {
        super(NAME);
    }

    @Override
    public void write(JsonGenerator jg, DocumentModel taskDoc) throws IOException {
        try (SessionWrapper wrapper = ctx.getSession(taskDoc)) {
            if (!wrapper.getSession().exists(taskDoc.getRef()) || taskDoc.getAdapter(Task.class) == null) {
                return;
            }

            CoreSession session = wrapper.getSession();
            Task task = taskDoc.getAdapter(Task.class);
            DocumentModel wfDoc = session.getDocument(new IdRef(task.getProcessId()));
            String docTitle = null;
            if (task.getTargetDocumentsIds().size() == 1) {
                DocumentModel attachedDoc = session.getDocument(new IdRef(task.getTargetDocumentsIds().get(0)));
                docTitle = attachedDoc.getTitle();
            }

            jg.writeStringField("workflowModelName", wfDoc.getTitle());
            jg.writeStringField("docTitle", docTitle);
        }
    }

}
