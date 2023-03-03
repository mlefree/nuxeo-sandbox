
package com.mlefree.nuxeo.sandbox.features;

import org.nuxeo.runtime.test.runner.TargetExtensions;

public class WorkflowTargetExtension extends TargetExtensions.Automation {
    @Override
    protected void initialize() {
        super.initialize();
        addTargetExtension("org.nuxeo.ecm.platform.routing.service", "routeModelImporter");
        addTargetExtension("org.nuxeo.ecm.platform.ec.notification.service.NotificationService", "templates");
    }
}
