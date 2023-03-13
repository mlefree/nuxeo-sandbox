package com.mlefree.nuxeo.sandbox.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.nuxeo.ecm.platform.routing.api.DocumentRoute;
import org.nuxeo.ecm.platform.routing.api.DocumentRouteElement;
import org.nuxeo.ecm.platform.routing.api.DocumentRoutingConstants;
import org.nuxeo.ecm.platform.routing.api.DocumentRoutingService;
import org.nuxeo.ecm.platform.routing.core.impl.GraphRoute;
import org.nuxeo.ecm.platform.task.Task;
import org.nuxeo.runtime.api.Framework;

import static org.nuxeo.ecm.platform.query.nxql.CoreQueryDocumentPageProvider.CORE_SESSION_PROPERTY;
import static org.nuxeo.ecm.platform.query.nxql.CoreQueryDocumentPageProvider.MAX_RESULTS_PROPERTY;
import static org.nuxeo.ecm.platform.query.nxql.CoreQueryDocumentPageProvider.PAGE_SIZE_RESULTS_KEY;
import static org.nuxeo.ecm.platform.routing.api.DocumentRoutingConstants.DOC_ROUTING_SEARCH_ALL_ROUTE_MODELS_PROVIDER_NAME;
import static org.nuxeo.ecm.platform.routing.api.DocumentRoutingConstants.DOC_ROUTING_SEARCH_ROUTE_MODELS_WITH_TITLE_PROVIDER_NAME;

public class WorkflowUtils {

    private static final Log log = LogFactory.getLog(WorkflowUtils.class);

    public static final String WF_BUTTON = "button";

    public static final String WF_TRANSITION = "transitionName";

    private WorkflowUtils() throws IllegalAccessException {
        throw new IllegalAccessException("Should not be instantiated.");
    }

    public static void followTransition(CoreSession session, Task task, String transitionName) {

        DocumentRoutingService documentRoutingService = Framework.getService(DocumentRoutingService.class);

        try {
            Map<String, Object> map = new HashMap<>();
            Map<String, Serializable> nodeVariables = new HashMap<>();
            nodeVariables.put(WF_BUTTON, transitionName);
            nodeVariables.put(WF_TRANSITION, transitionName);
            map.put(Constants.VAR_WORKFLOW_NODE, nodeVariables);
            documentRoutingService.endTask(session, task, map, transitionName);
            session.save();
        } catch (Exception e) {
            throw new NuxeoException(e);
        }
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
        routeDoc = routeDoc.getCoreSession().getDocument(routeDoc.getRef());
        GraphRoute routeInstance = routeDoc.getAdapter(GraphRoute.class);
        return routeInstance.getVariables();
    }

    public static GraphRoute getRouteIdRelatedWorkflow(CoreSession session, String routeId) {
        DocumentModel routeDoc = session.getDocument(new IdRef(routeId));
        return routeDoc.getAdapter(GraphRoute.class);
    }

    public static List<DocumentRoute> getDocumentRelatedWorkflows(DocumentModel document, CoreSession session) {
        final String query = String.format(
                "SELECT * FROM %s WHERE docri:participatingDocuments/* = '%s' AND ecm:currentLifeCycleState = '%s'",
                DocumentRoutingConstants.DOCUMENT_ROUTE_DOCUMENT_TYPE, document.getId(),
                DocumentRouteElement.ElementLifeCycleState.running);
        DocumentModelList documentModelList = session.query(query);
        List<DocumentRoute> result = new ArrayList<>();
        for (DocumentModel documentModel : documentModelList) {
            result.add(documentModel.getAdapter(GraphRoute.class));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static List<DocumentModel> searchRouteModels(CoreSession session, String searchString) {
        PageProviderService pageProviderService = Framework.getService(PageProviderService.class);
        Map<String, Serializable> props = new HashMap<>();
        props.put(MAX_RESULTS_PROPERTY, PAGE_SIZE_RESULTS_KEY);
        props.put(CORE_SESSION_PROPERTY, (Serializable) session);
        PageProvider<DocumentModel> pageProvider;
        if (StringUtils.isEmpty(searchString)) {
            pageProvider = (PageProvider<DocumentModel>) pageProviderService.getPageProvider(
                    DOC_ROUTING_SEARCH_ALL_ROUTE_MODELS_PROVIDER_NAME, null, null, 0L, props);
        } else {
            pageProvider = (PageProvider<DocumentModel>) pageProviderService.getPageProvider(
                    DOC_ROUTING_SEARCH_ROUTE_MODELS_WITH_TITLE_PROVIDER_NAME, null, null, 0L, props,
                    searchString + '%');
        }
        List<DocumentModel> allRouteModels = new ArrayList<>(pageProvider.getCurrentPage());
        while (pageProvider.isNextPageAvailable()) {
            pageProvider.nextPage();
            allRouteModels.addAll(pageProvider.getCurrentPage());
        }
        return allRouteModels;
    }

    public static List<DocumentRoute> getAllWorkflows(CoreSession session) {
        List<DocumentModel> routeModels = searchRouteModels(session, "");
        return routeModels.stream()
                          .map(document -> document.getAdapter(DocumentRoute.class))
                          .collect(Collectors.toList());
    }

}
