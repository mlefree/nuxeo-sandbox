package com.mlefree.nuxeo.sandbox.features;

import static com.mlefree.nuxeo.sandbox.features.MleFeature.openSessionAsUser;
import static com.mlefree.nuxeo.sandbox.utils.UsersConfiguration.ADMIN;
import static com.mlefree.nuxeo.sandbox.utils.WorkflowUtils.followTransition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.audit.AuditFeature;
import org.nuxeo.ecm.platform.routing.api.DocumentRoute;
import org.nuxeo.ecm.platform.routing.api.DocumentRouteElement;
import org.nuxeo.ecm.platform.routing.api.DocumentRoutingConstants;
import org.nuxeo.ecm.platform.routing.api.DocumentRoutingService;
import org.nuxeo.ecm.platform.routing.core.impl.GraphRoute;
import org.nuxeo.ecm.platform.routing.test.DocumentRoutingFeature;
import org.nuxeo.ecm.platform.task.Task;
import org.nuxeo.ecm.platform.task.TaskService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.RunnerFeature;

import com.mlefree.nuxeo.sandbox.utils.WorkflowUtils;

@Features({ AutomationFeature.class, DocumentRoutingFeature.class, AuditFeature.class })
// @Deploy({ "org.nuxeo.ecm.platform.routing.core:OSGI-INF/document-routing-nxfilemanager-plugins-contrib.xml" })
@Deploy("org.nuxeo.ecm.platform.task.api")
@Deploy("org.nuxeo.ecm.platform.task.core")
// @Deploy("org.nuxeo.ecm.platform.routing.default")
@Deploy("org.nuxeo.ecm.platform.content.template")
@Deploy("org.nuxeo.ecm.platform.routing.api")
@Deploy("org.nuxeo.ecm.platform.routing.core")
@Deploy("org.nuxeo.ecm.automation.core")
@Deploy("org.nuxeo.ecm.platform.usermanager")
@Deploy("org.nuxeo.ecm.platform.userworkspace")
public class StudioWorkflowFeature implements RunnerFeature {


    public static List<DocumentRoute> getRelatedWithdrawableWorkflows(CoreSession userSession, DocumentModel doc) {
        List<DocumentRoute> workflows = null;
        /*
         * try (CloseableCoreSession systemSession = MleFeature.openSessionAsUser("Administrator")) {
         * DocumentRoutingService documentRoutingService = Framework.getService(DocumentRoutingService.class); workflows
         * = documentRoutingService.getDocumentRelatedWorkflows(doc, systemSession); workflows =
         * workflows.stream().filter(route -> { GraphRoute routeInstance =
         * route.getDocument().getAdapter(GraphRoute.class); Map<String, Serializable> workflowVariables =
         * routeInstance.getVariables(); String[] users = (String[])
         * workflowVariables.get(WF_PUBLISH_USERS_WHO_CAN_WITHDRAW); if (users == null) { return false; } return
         * Arrays.asList(users).contains(userSession.getPrincipal().getName()); }).collect(Collectors.toList()); }
         */
        return workflows;
    }

    public static String startWorkflowAndGetRouteId(DocumentRoutingService routing, CoreSession userSession,
            DocumentModel doc, String workflowName) {

        doc = doc.getCoreSession().getDocument(doc.getRef());
        Map<String, Serializable> map = new HashMap<>();
        // map.put(WF_PUBLISH_COMPANY, COMPANY_A);

        String routeId = routing.createNewInstance(workflowName, Collections.singletonList(doc.getId()), map,
                userSession, true);
        userSession.save();
        return routeId;

    }

    public static Map<String, Serializable> getWorkflowVariables(DocumentRoutingService routing, DocumentModel doc,
            CoreSession userSession) {

        doc = doc.getCoreSession().getDocument(doc.getRef());
        NuxeoPrincipal user = userSession.getPrincipal();
        List<Task> tasks = Framework.getService(TaskService.class).getTaskInstances(doc, user, userSession);
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        Task task1 = tasks.get(0);
        assertEquals("RoutingTask", task1.getDocument().getType());
        try (CloseableCoreSession adminSession = openSessionAsUser(ADMIN.getUserName())) {
            List<DocumentModel> docs = routing.getWorkflowInputDocuments(adminSession, task1);
            assertEquals(doc.getId(), docs.get(0).getId());

            String processId = task1.getProcessId();
            DocumentModel routeDoc = adminSession.getDocument(new IdRef(processId));
            return WorkflowUtils.getWorkflowVariables(routeDoc);
        }

    }

    public static void followWorkflowTransition(DocumentModel doc, CoreSession userSession, String transition) {
        NuxeoPrincipal user = userSession.getPrincipal();
        doc = doc.getCoreSession().getDocument(doc.getRef());
        List<Task> tasks = Framework.getService(TaskService.class).getTaskInstances(doc, user, userSession);
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        Task task = tasks.get(0);

        followTransition(userSession, task, transition);
        userSession.save();
    }

    public static void assertYourPublishWorkflowIsFinished(String routeId, DocumentModel doc, CoreSession sessionUser) {

        // Assert.assertEquals(lifecycle, sessionUser.getDocument(doc.getRef()).getCurrentLifeCycleState());

        try (CloseableCoreSession adminSession = openSessionAsUser(ADMIN.getUserName())) {
            DocumentRoute route = adminSession.getDocument(new IdRef(routeId)).getAdapter(DocumentRoute.class);
            assertTrue(route.isDone());
        }
    }

}
