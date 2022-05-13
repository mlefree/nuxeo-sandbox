package com.mlefree.nuxeo.sandbox.searches;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.automation.test.EmbeddedAutomationServerFeature;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

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
public class TestMemberPageProvider {

    @Inject
    private PageProviderService pageProviderService;

    @Test
    @Ignore
    public void testGetCurrentPage() {

        Object[] params = new Object[] {}; // "tenant", "committeeid" };
        PageProvider<DocumentModel> provider = getPageProvider("pp_mainstream_default_search", params);

        Assert.assertEquals(31, provider.getResultsCount());
    }

    private PageProvider<DocumentModel> getPageProvider(String pageProviderName, Object[] params) {
        Map<String, Serializable> properties = Collections.emptyMap();
        return (PageProvider<DocumentModel>) pageProviderService.getPageProvider(pageProviderName, null, null, null,
                properties, params);

    }

}
