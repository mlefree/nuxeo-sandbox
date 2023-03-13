package com.mlefree.nuxeo.sandbox.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.Principal;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.context.ContextHelper;
import org.nuxeo.ecm.automation.context.ContextService;
import org.nuxeo.ecm.automation.core.scripting.Scripting;
import org.nuxeo.ecm.automation.features.PlatformFunctions;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.mlefree.nuxeo.sandbox.features.MleFeature;

@RunWith(FeaturesRunner.class)
@Features({ MleFeature.class })
public class TestIntegration {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Inject
    ContextService ctxService;

    @Test
    public void testFnIntegration() throws Exception {

        OperationContext ctx = new OperationContext(session);
        Map<String, ContextHelper> contextHelperList = ctxService.getHelperFunctions();
        PlatformFunctions functions = (PlatformFunctions) contextHelperList.get("Fn");
        assertEquals(functions, Scripting.newExpression("Fn").eval(ctx));
        assertTrue(functions instanceof PlatformFunctions);

        NuxeoPrincipal np = (functions).getPrincipal("Administrator");
        assertEquals("Administrator", np.getName());
        assertEquals("Administrator",
                ((Principal) Scripting.newExpression("Fn.getPrincipal(\"Administrator\")").eval(ctx)).getName());

        String seqValueStr = Scripting.newExpression("Fn.getNextId(\"dc_description\", \"mongoDBSeq\")").eval(ctx).toString();
        int seqValue = Integer.parseInt(seqValueStr);
        assertTrue(seqValue > 0);
    }

}
