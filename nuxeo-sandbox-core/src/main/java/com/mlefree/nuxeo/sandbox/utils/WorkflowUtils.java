package com.mlefree.nuxeo.sandbox.utils;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.platform.routing.api.DocumentRoute;
import org.nuxeo.ecm.platform.routing.api.DocumentRouteElement;
import org.nuxeo.ecm.platform.routing.api.DocumentRoutingConstants;
import org.nuxeo.ecm.platform.routing.api.DocumentRoutingService;
import org.nuxeo.ecm.platform.routing.core.impl.GraphRoute;
import org.nuxeo.ecm.platform.task.Task;
import org.nuxeo.runtime.api.Framework;

public class WorkflowUtils {

    private static final Log log = LogFactory.getLog(WorkflowUtils.class);

    private static final String[] WORKFLOW_NAMES = { "PublishWorkflow", "RevisionWorkflow", "UnpublishWorkflow" };

    public static final String WF_PUBLISH_BUTTON = "button";

    public static final String WF_PUBLISH_TRANSITION = "transitionName";

    private WorkflowUtils() throws IllegalAccessException {
        throw new IllegalAccessException("Should not be instantiated.");
    }

    public static void followTransition(CoreSession session, Task task, String transitionName) {

        DocumentRoutingService documentRoutingService = Framework.getService(DocumentRoutingService.class);

        try {
            Map<String, Object> map = new HashMap<>();
            Map<String, Serializable> nodeVariables = new HashMap<>();
            nodeVariables.put(WF_PUBLISH_BUTTON, transitionName);
            nodeVariables.put(WF_PUBLISH_TRANSITION, transitionName);
            map.put(Constants.VAR_WORKFLOW_NODE, nodeVariables);
            documentRoutingService.endTask(session, task, map, transitionName);
            session.save();
        } catch (Exception e) {
            throw new NuxeoException(e);
        }
    }

    public static List<DocumentRoute> getRelatedWorkflowsThatRequireReminder(CoreSession session) {
        List<DocumentRoute> workflows = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(Calendar.getInstance().getTime());
        DocumentModelList documentModelList = new DocumentModelListImpl(0);
        for (String workflowName : WORKFLOW_NAMES) {
            final String query = String.format(
                    "SELECT * FROM %s WHERE ecm:currentLifeCycleState = '%s' AND %s:%s <= TIMESTAMP '%s'",
                    DocumentRoutingConstants.DOCUMENT_ROUTE_DOCUMENT_TYPE,
                    DocumentRouteElement.ElementLifeCycleState.running, "var_" + workflowName,
                    "WF_PUBLISH_NOTIFICATION_DUE_DATE", now);
            documentModelList.addAll(session.query(query));
        }
        for (DocumentModel documentModel : documentModelList) {
            DocumentRoute route = documentModel.getAdapter(GraphRoute.class);
            workflows.add(route);
            if (log.isDebugEnabled()) {
                log.debug(String.format(" [%s - %s ] Remind to act on Workflow (related documents %s)",
                        documentModel.getId(), route.getName(), route.getAttachedDocuments().toString()));
            }
        }

        return workflows;
    }

    public static GraphRoute getTaskRelatedWorkflow(CoreSession session, Task task) {
        String routeId = task.getProcessId();
        DocumentModel routeDoc = session.getDocument(new IdRef(routeId));
        return routeDoc.getAdapter(GraphRoute.class);
    }

    public static Map<String, Serializable> getTaskRelatedWorkflowVariables(CoreSession session, Task task) {
        return getTaskRelatedWorkflow(session, task).getVariables();
    }

    public static Map<String, Serializable> getWorkflowVariables(DocumentModel routeDoc) {
        GraphRoute routeInstance = routeDoc.getAdapter(GraphRoute.class);
        return routeInstance.getVariables();
    }

    public static String getTaskRelatedCompany(CoreSession session, Task task) {
        String company = (String) getTaskRelatedWorkflowVariables(session, task).get("WF_PUBLISH_COMPANY");
        if (company != null && company.startsWith("group:")) {
            company = company.substring(6);
        }
        return company;
    }

    public static GraphRoute getRouteIdRelatedWorkflow(CoreSession session, String routeId) {
        DocumentModel routeDoc = session.getDocument(new IdRef(routeId));
        return routeDoc.getAdapter(GraphRoute.class);
    }
}
