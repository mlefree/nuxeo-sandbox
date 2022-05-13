/*
 * (C) Copyright 2019 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Contributors:
 *      Kevin Leturc <kleturc@nuxeo.com>
 */

package com.mlefree.nuxeo.sandbox.searches;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response.StatusType;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.automation.test.EmbeddedAutomationServerFeature;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.restapi.test.BaseTest;
import org.nuxeo.ecm.restapi.test.RestServerInit;
import org.nuxeo.elasticsearch.test.RepositoryElasticSearchFeature;
import org.nuxeo.jaxrs.test.CloseableClientResponse;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
// @Features(SearchRestFeature.class)
// @Deploy("org.nuxeo.ecm.core.api.tests:OSGI-INF/test-documentmodel-secured-types-contrib.xml")
@RepositoryConfig(cleanup = Granularity.METHOD, init = RestServerInit.class)
@Features({ AutomationFeature.class, EmbeddedAutomationServerFeature.class, RepositoryElasticSearchFeature.class })
@Deploy("org.nuxeo.ecm.automation.server")
@Deploy("org.nuxeo.ecm.automation.io")
@Deploy("org.nuxeo.ecm.platform.forms.layout.export")
@Deploy("org.nuxeo.ecm.webengine.core")
@Deploy("org.nuxeo.ecm.webengine.jaxrs")
@Deploy("org.nuxeo.elasticsearch.core")
@Deploy("org.nuxeo.ecm.platform.query.api")
@Deploy("org.nuxeo.ecm.core.management")
@Deploy("org.nuxeo.ecm.platform.search.core")
@Deploy("org.nuxeo.elasticsearch.core.test:elasticsearch-test-contrib.xml")
// @Deploy("com.mlefree.nuxeo.sandbox.nuxeo-sandbox-core:pageprovider-test-contrib.xml")
public class SearchSecuredPropertyTest extends BaseTest {

    @Test
    @Ignore
    public void testUserCanSearchUsingSearchDocument() {
        this.service = getServiceFor("Administrator", "Administrator");
        try (CloseableClientResponse response = getResponse(RequestType.GET, "search/pp/default_search/execute",
                // try (CloseableClientResponse response = getResponse(RequestType.GET,
                // "search/pp/pp_mainstream_default_search_test/execute",
                multiOf("secured:scalar", "Administrator"))) {
            StatusType status = response.getStatusInfo();
            assertEquals("HTTP Reason: " + status.getReasonPhrase(), SC_OK, status.getStatusCode());
        }
    }

}
