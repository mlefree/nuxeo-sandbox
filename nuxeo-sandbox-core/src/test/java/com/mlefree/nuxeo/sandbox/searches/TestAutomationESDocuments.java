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
import org.nuxeo.ecm.automation.test.EmbeddedAutomationServerFeature;
import org.nuxeo.ecm.automation.test.HttpAutomationSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.mlefree.nuxeo.sandbox.features.MleFeature;
import com.mlefree.nuxeo.sandbox.features.RepositoryElasticSearchFeature;

@RunWith(FeaturesRunner.class)
@Features({ MleFeature.class, EmbeddedAutomationServerFeature.class,
        RepositoryElasticSearchFeature.class })
@Deploy("org.nuxeo.elasticsearch.core")
@Deploy("org.nuxeo.ecm.platform.query.api")
@Deploy("org.nuxeo.ecm.core.management")
@Deploy("org.nuxeo.ecm.platform.search.core")
@Deploy("org.nuxeo.elasticsearch.core.test:elasticsearch-test-contrib.xml")
public class TestAutomationESDocuments {

    @Inject
    protected HttpAutomationSession httpAutomationSession;

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;


    @Test
    public void fake() throws Exception {
        assertNotNull(httpAutomationSession);
    }

    @Test
    @Ignore
    public void iCanPerformESQLPageProviderOperationOnRepository() throws Exception {
        Properties namedParameters = new Properties(Map.of("defaults:dc_nature_agg", "[\"article\"]"));
        JsonNode node = httpAutomationSession.newRequest(DocumentPageProviderOperation.ID)
                               .set("namedParameters", namedParameters)
                               .set("providerName", "default_search")
                               .execute();
        assertEquals(node.get("pageSize").asInt(), 20);
        assertEquals(node.get("resultsCount").asInt(), 11);
    }

    @Test
    @Ignore
    public void iCanSkipAggregatesOnESQLPageProviderOperationOnRepository() throws Exception {
        JsonNode node = httpAutomationSession.newRequest(DocumentPageProviderOperation.ID)
                               .set("providerName", "aggregates_1")
                               .execute();
        assertTrue(node.has("aggregations"));

        node = httpAutomationSession.newRequest(DocumentPageProviderOperation.ID)
                      .setHeader(PageProvider.SKIP_AGGREGATES_PROP, "true")
                      .set("providerName", "aggregates_1")
                      .execute();
        assertFalse(node.has("aggregations"));
    }

    @Test
    @Ignore
    public void shouldTestMainStream() throws OperationException {

        OperationContext ctx = new OperationContext(session);
        HashMap<String, String> params = new HashMap<>();
        params.put("providerName", "pp_mainstream_default_search");

        PaginableDocumentModelListImpl result = (PaginableDocumentModelListImpl) automationService.run(ctx,
                DocumentPageProviderOperation.ID, params);

        // test page size
        assertEquals(2, result.getPageSize());
        assertEquals(2, result.getNumberOfPages());
        assertEquals(2, result.size());

        assertEquals("todo", result);
    }

}
