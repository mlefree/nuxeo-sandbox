package com.mlefree.nuxeo.sandbox.features;

import static com.mlefree.nuxeo.sandbox.constants.StudioConstant.BUNDLE_NAME;

import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.RunnerFeature;
import org.nuxeo.runtime.transaction.TransactionHelper;

import com.mlefree.nuxeo.sandbox.MleRepositoryInit;

@Features({ AutomationFeature.class })
@Deploy({ "com.mlefree.nuxeo.sandbox.nuxeo-sandbox-core" })
@Deploy("org.nuxeo.ecm.core:OSGI-INF/uidgenerator-service.xml")
@Deploy("org.nuxeo.ecm.core:OSGI-INF/uidgenerator-keyvalue-config.xml")
@Deploy("com.mlefree.nuxeo.sandbox.nuxeo-sandbox-core:test-uidseq.xml")
@Deploy({ BUNDLE_NAME })
@RepositoryConfig(init = MleRepositoryInit.class, cleanup = Granularity.METHOD)
public class MleFeature implements RunnerFeature {

    public static CloseableCoreSession openSessionAsUser(String userName) {
        UserManager userManager = Framework.getService(UserManager.class);
        return CoreInstance.openCoreSession(null, userManager.getPrincipal(userName));
    }

    public static void waitForAsyncExec() {
        // Wait for the listener to be called
        if (TransactionHelper.isTransactionActiveOrMarkedRollback()) {
            TransactionHelper.commitOrRollbackTransaction();
            TransactionHelper.startTransaction();
        }
        Framework.getService(EventService.class).waitForAsyncCompletion();
    }

    public static void waitForAsyncExec(int forcedTimeOutInSec) {
        waitForAsyncExec();
        try {
            Thread.sleep(forcedTimeOutInSec * 1000L);
        } catch (Exception ignored) {
        }
    }

    public static void nextTransaction() {
        if (TransactionHelper.isTransactionActiveOrMarkedRollback()) {
            TransactionHelper.commitOrRollbackTransaction();
            TransactionHelper.startTransaction();
        }
    }
}
