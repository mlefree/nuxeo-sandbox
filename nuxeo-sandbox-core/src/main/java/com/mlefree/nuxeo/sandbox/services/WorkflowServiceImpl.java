package com.mlefree.nuxeo.sandbox.services;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.routing.api.DocumentRoute;
import org.nuxeo.ecm.platform.routing.core.impl.GraphRoute;
import org.nuxeo.ecm.platform.task.Task;
import org.nuxeo.ecm.platform.task.TaskService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.DefaultComponent;

import static com.mlefree.nuxeo.sandbox.utils.UserManagementUtils.openSessionAsSystem;
import static com.mlefree.nuxeo.sandbox.utils.WorkflowUtils.followTransition;
import static com.mlefree.nuxeo.sandbox.utils.WorkflowUtils.getTaskRelatedWorkflow;
import static com.mlefree.nuxeo.sandbox.utils.WorkflowUtils.getTaskRelatedWorkflowVariables;
import static com.mlefree.nuxeo.sandbox.utils.WorkflowUtils.getWorkflowVariables;

public class WorkflowServiceImpl extends DefaultComponent implements WorkflowService {

    private static final Log log = LogFactory.getLog(WorkflowServiceImpl.class);

    protected static String getTransitionNameToApply(String taskName, NuxeoPrincipal user, String company,
            DocumentModel doc) {
        String transitionName = null;
        switch (taskName) {
        case "workflow.company.review":

            transitionName = "WF_PUBLISH_TRANSITION_BACK_TO_FACTORY";

            break;
        default:
            log.warn(String.format("No valid transition found for task %s", taskName));
        }
        return transitionName;
    }

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
    public void setActive(CoreSession session, OperationContext ctx, DocumentModel doc) {
        try (CloseableCoreSession systemSession = openSessionAsSystem()) {


            // first fill context
            if (ctx.get(Constants.VAR_WORKFLOW) != null) {
                // ((Map<String, Serializable>) ctx.get(Constants.VAR_WORKFLOW)).put(name, (Serializable) value);
            }
            // get workflow instance id from context if not in automation parameters
            String  workflowInstanceId = (String) ctx.get("workflowInstanceId");

            // finally save graph variables
            DocumentModel workflowInstance = session.getDocument(new IdRef(workflowInstanceId));
            GraphRoute graph = workflowInstance.getAdapter(GraphRoute.class);
            Map<String, Serializable> vars = graph.getVariables();
            vars.put("active", false);
            graph.setVariables(vars);

            /*

            List<Task> tasks = Framework.getService(TaskService.class)
                                        .getTaskInstances(doc, systemSession.getPrincipal(), systemSession);

            GraphRoute route = getTaskRelatedWorkflow(systemSession, tasks.get(0));
            Map<String, Serializable> vars = getTaskRelatedWorkflowVariables(systemSession, tasks.get(0));
            boolean active = (boolean) vars.get("active");
            vars.put("active", false);

            route.setVariables(vars);

            systemSession.saveDocument(route.getDocument());*/

        } catch (Exception e) {
            throw new NuxeoException(
                    String.format("It wasn't possible to resume document %s: %s", doc.getId(), e.getMessage()));
        }
    }

}
