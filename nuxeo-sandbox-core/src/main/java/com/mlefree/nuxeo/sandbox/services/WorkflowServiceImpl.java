package com.mlefree.nuxeo.sandbox.services;

import static com.mlefree.nuxeo.sandbox.utils.UserManagementUtils.openSessionAsSystem;
import static com.mlefree.nuxeo.sandbox.utils.WorkflowUtils.followTransition;
import static com.mlefree.nuxeo.sandbox.utils.WorkflowUtils.getAllWorkflows;
import static java.lang.Boolean.TRUE;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.platform.routing.api.DocumentRoute;
import org.nuxeo.ecm.platform.routing.api.DocumentRouteElement;
import org.nuxeo.ecm.platform.routing.api.DocumentRoutingConstants;
import org.nuxeo.ecm.platform.routing.core.impl.GraphRoute;
import org.nuxeo.ecm.platform.task.Task;
import org.nuxeo.ecm.platform.task.TaskService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.DefaultComponent;

public class WorkflowServiceImpl extends DefaultComponent implements WorkflowService {

    private static final Log log = LogFactory.getLog(WorkflowServiceImpl.class);

    public static final String WF_EVENTS_CATEGORY = "workflow";

    public static final String WF_ACTIVE_STATUS = "active";

    public static final String WF_ACTIVE_SECONDS_COUNT = "activeSecondsCount";

    public static final String WF_ACTIVE_LAST_UPDATE = "activeLastUpdate";

    @Override
    public void followResumeTransition(CoreSession session, DocumentModel doc) {

        try (CloseableCoreSession systemSession = openSessionAsSystem()) {
            List<Task> tasks = Framework.getService(TaskService.class)
                                        .getTaskInstances(doc, systemSession.getPrincipal(), systemSession);
            followTransition(systemSession, tasks.get(0), "resume");
        } catch (Exception e) {
            throw new NuxeoException(
                    String.format("It wasn't possible to resume document %s: %s", doc.getId(), e.getMessage()));
        }
    }

    @Override
    public void setActive(CoreSession session, DocumentModel wfDoc) {
        GraphRoute graph = wfDoc.getAdapter(GraphRoute.class);
        Date now = new Date();

        Map<String, Serializable> vars = graph.getVariables();
        Boolean active = (Boolean) vars.get(WF_ACTIVE_STATUS);
        Date lastUpdate = (Date) vars.get(WF_ACTIVE_LAST_UPDATE);

        if (lastUpdate == null) {
            vars.put(WF_ACTIVE_LAST_UPDATE, now);
        }

        if (TRUE.equals(active)) {
            graph.setVariables(vars);
            return;
        }

        vars.put(WF_ACTIVE_STATUS, true);
        vars.put(WF_ACTIVE_LAST_UPDATE, now);

        graph.setVariables(vars);
    }

    @Override
    public void setOnHold(CoreSession session, DocumentModel wfDoc) {
        GraphRoute graph = wfDoc.getAdapter(GraphRoute.class);
        Date now = new Date();

        Map<String, Serializable> vars = graph.getVariables();
        Boolean active = (Boolean) vars.get(WF_ACTIVE_STATUS);
        Date lastUpdate = (Date) vars.get(WF_ACTIVE_LAST_UPDATE);
        long seconds = 0;
        if (vars.get(WF_ACTIVE_SECONDS_COUNT) != null) {
            seconds = (long) vars.get(WF_ACTIVE_SECONDS_COUNT);
        }

        if (!TRUE.equals(active)) {
            return;
        }

        if (lastUpdate == null) {
            lastUpdate = now;
        }

        seconds += (now.getTime() - lastUpdate.getTime()) / 1000 % 60;
        vars.put(WF_ACTIVE_STATUS, false);
        vars.put(WF_ACTIVE_SECONDS_COUNT, seconds);
        vars.put(WF_ACTIVE_LAST_UPDATE, null);

        graph.setVariables(vars);
    }

    @Override
    public DocumentModelList getAllRunningWorkflow(CoreSession session, boolean activeOnly) {

        DocumentModelList activeWfs = new DocumentModelListImpl(0);
        List<DocumentRoute> wfs = getAllWorkflows(session);
        String filter = "";

        for (DocumentRoute wf : wfs) {
            try {
                String workflowName = wf.getName();
                if (activeOnly) {
                    filter = String.format("AND %s:active = 1", "var_" + workflowName);
                }
                final String query = String.format(
                        "SELECT * FROM %s WHERE ecm:currentLifeCycleState = '%s' AND ecm:name = '%s' %s",
                        DocumentRoutingConstants.DOCUMENT_ROUTE_DOCUMENT_TYPE,
                        DocumentRouteElement.ElementLifeCycleState.running, workflowName, filter);
                activeWfs.addAll(session.query(query));
            } catch (Exception ignored) {
            }
        }

        return activeWfs;
    }

    @Override
    public DocumentModelList getAllRunningTasks(CoreSession session, boolean activeOnly) {

        TaskService taskService = Framework.getService(TaskService.class);
        List<Task> tasks = taskService.getAllCurrentTaskInstances(session, Collections.emptyList());
        Stream<Task> tasksStream = tasks.stream();

        if (activeOnly) {
            tasksStream = tasksStream.filter(task -> {
                DocumentModel wfDoc = session.getDocument(new IdRef(task.getProcessId()));
                Boolean active = (Boolean) wfDoc.getAdapter(GraphRoute.class).getVariables().get(WF_ACTIVE_STATUS);
                return TRUE.equals(active);
            });
        }

        return tasksStream.map(task -> {
            DocumentModel doc = task.getDocument();
            // DocumentModel wfDoc = session.getDocument(new IdRef(task.getProcessId()));
            // String docTitle = null;
            // if (task.getTargetDocumentsIds().size() == 1) {
            // DocumentModel attachedDoc = session.getDocument(new IdRef(task.getTargetDocumentsIds().get(0)));
            // docTitle = attachedDoc.getTitle();
            // }
            // doc.putContextData("workflowModelName", wfDoc.getTitle());
            // doc.putContextData("docTitle", docTitle);
            return doc;
        })
                          .sorted(Comparator.comparing(doc -> (Calendar) doc.getPropertyValue("dc:created")))
                          .collect(Collectors.toCollection(DocumentModelListImpl::new));
    }

    @Override
    public boolean isTaskActive(CoreSession session, DocumentModel taskDoc) {
        Task task = taskDoc.getAdapter(Task.class);
        DocumentModel wfDoc = session.getDocument(new IdRef(task.getProcessId()));
        Boolean active = (Boolean) wfDoc.getAdapter(GraphRoute.class).getVariables().get(WF_ACTIVE_STATUS);
        return TRUE.equals(active);
    }

}
