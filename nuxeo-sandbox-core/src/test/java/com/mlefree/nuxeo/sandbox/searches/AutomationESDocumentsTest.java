package com.mlefree.nuxeo.sandbox.searches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.operations.services.DocumentPageProviderOperation;
import org.nuxeo.ecm.automation.core.util.Properties;
import org.nuxeo.ecm.automation.jaxrs.io.documents.PaginableDocumentModelListImpl;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.automation.test.EmbeddedAutomationServerFeature;
import org.nuxeo.ecm.automation.test.HttpAutomationSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.TransactionalFeature;

import com.fasterxml.jackson.databind.JsonNode;
import com.mlefree.nuxeo.sandbox.MleFeature;
import com.mlefree.nuxeo.sandbox.RepositoryElasticSearchFeature;

@RunWith(FeaturesRunner.class)
@Features({ MleFeature.class, AutomationFeature.class, EmbeddedAutomationServerFeature.class,
        RepositoryElasticSearchFeature.class })
// @Deploy("org.nuxeo.ecm.automation.server")
// @Deploy("org.nuxeo.ecm.automation.io")
// @Deploy("org.nuxeo.ecm.platform.forms.layout.export")
// @Deploy("org.nuxeo.ecm.webengine.core")
// @Deploy("org.nuxeo.ecm.webengine.jaxrs")
@Deploy("org.nuxeo.elasticsearch.core")
@Deploy("org.nuxeo.ecm.platform.query.api")
@Deploy("org.nuxeo.ecm.core.management")
@Deploy("org.nuxeo.ecm.platform.search.core")
@Deploy("org.nuxeo.elasticsearch.core.test:elasticsearch-test-contrib.xml")
// @Deploy("com.mlefree.nuxeo.sandbox.nuxeo-sandbox-core:pageprovider-test-contrib.xml")
// @Deploy("org.nuxeo.elasticsearch.core.test:pageprovider2-test-contrib.xml")
// @Deploy("org.nuxeo.elasticsearch.core.test:pageprovider2-coretype-test-contrib.xml")
// @RepositoryConfig(cleanup = Granularity.METHOD, init = MleRepositoryInit.class)
public class AutomationESDocumentsTest {

    @Inject
    protected HttpAutomationSession session;

    @Inject
    protected CoreSession session_;

    @Inject
    protected AutomationService automationService;

    @Inject
    protected TransactionalFeature transactionalFeature;

    @Test
    public void fake() throws Exception {
        assertNotNull(session);
    }

    @Test
    @Ignore
    public void iCanPerformESQLPageProviderOperationOnRepository() throws Exception {
        Properties namedParameters = new Properties(Map.of("defaults:dc_nature_agg", "[\"article\"]"));
        JsonNode node = session.newRequest(DocumentPageProviderOperation.ID)
                               .set("namedParameters", namedParameters)
                               .set("providerName", "default_search")
                               .execute();
        assertEquals(node.get("pageSize").asInt(), 20);
        assertEquals(node.get("resultsCount").asInt(), 11);
    }

    @Test
    @Ignore
    public void iCanSkipAggregatesOnESQLPageProviderOperationOnRepository() throws Exception {
        JsonNode node = session.newRequest(DocumentPageProviderOperation.ID)
                               .set("providerName", "aggregates_1")
                               .execute();
        assertTrue(node.has("aggregations"));

        node = session.newRequest(DocumentPageProviderOperation.ID)
                      .setHeader(PageProvider.SKIP_AGGREGATES_PROP, "true")
                      .set("providerName", "aggregates_1")
                      .execute();
        assertFalse(node.has("aggregations"));
    }

    @Test
    @Ignore
    public void shouldTestMainStream() throws OperationException {

        OperationContext ctx = new OperationContext(session_);
        // ctx.setInput(folder1);
        HashMap<String, String> params = new HashMap<>();
        params.put("providerName", "pp_mainstream_default_search");
        // params.put("providerName", "default_search");
        // params.put("providerName", "tenant_all_committee_provider");
        // params.put("providerName", "TEST_PP_ALL_NOTE");

        PaginableDocumentModelListImpl result = (PaginableDocumentModelListImpl) automationService.run(ctx,
                DocumentPageProviderOperation.ID, params);

        // test page size
        assertEquals(2, result.getPageSize());
        assertEquals(2, result.getNumberOfPages());
        assertEquals(2, result.size());

        assertEquals("todo", result);
    }

}
