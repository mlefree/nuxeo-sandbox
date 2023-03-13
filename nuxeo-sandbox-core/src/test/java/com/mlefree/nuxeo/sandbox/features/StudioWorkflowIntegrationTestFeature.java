package com.mlefree.nuxeo.sandbox.features;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.nuxeo.ecm.core.test.FakeSmtpMailServerFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.LogCaptureFeature;
import org.nuxeo.runtime.test.runner.RunnerFeature;

import com.dumbster.smtp.SmtpMessage;

@Features({ StudioWorkflowFeature.class, FakeSmtpMailServerFeature.class, LogCaptureFeature.class })
// @Deploy("org.nuxeo.ecm.platform.notification.core")
// @Deploy("org.nuxeo.ecm.platform.notification.api")
// @Deploy("org.nuxeo.ecm.platform.url.api")
// @Deploy("org.nuxeo.ecm.platform.url.core")
// @Features({ })
// @Deploy("org.nuxeo.ecm.platform.routing.core:OSGI-INF/test-sql-directories-contrib.xml")
// @Deploy("org.nuxeo.ecm.platform.routing.core:OSGI-INF/test-graph-operations-contrib.xml")
// @Deploy("org.nuxeo.ecm.platform.routing.core:OSGI-INF/test-graph-types-contrib.xml")
// @Deploy("org.nuxeo.ecm.platform.audit:OSGI-INF/core-type-contrib.xml")
public class StudioWorkflowIntegrationTestFeature implements RunnerFeature {

    protected static SmtpMessage getIndexMessage(int index) {
        List<SmtpMessage> mails = FakeSmtpMailServerFeature.server.getReceivedEmails();
        return mails.get(index);
    }

    public static void assertNotificationContains(int index, String body) {
        assertThat(FakeSmtpMailServerFeature.server.getReceivedEmails().size()).isGreaterThanOrEqualTo(index);
        SmtpMessage mailReceived = getIndexMessage(index);
        String mailBody = mailReceived.getBody();
        boolean contains = true;
        if (!mailBody.contains(body)) {
            byte[] decodedBytes = Base64.getDecoder().decode(mailBody);
            String decodedString = new String(decodedBytes);
            contains = decodedString.contains(body);
        }
        assertThat(contains).isTrue();
    }

    public static void assertNotificationTo(int index, List<String> to) {
        assertThat(FakeSmtpMailServerFeature.server.getReceivedEmails().size()).isGreaterThanOrEqualTo(index);
        SmtpMessage mailReceived = getIndexMessage(index);
        List<String> headerTo = mailReceived.getHeaderValues("To");
        headerTo = Arrays.stream(headerTo.get(0).split(",")).map(String::trim).collect(Collectors.toList());
        assertThat(headerTo).containsAll(to);
        assertThat(headerTo.size()).isEqualTo(to.size()).withFailMessage(headerTo.toString() + " <> " + to.toString());
    }

    public static void assertNotificationSubject(int index, String subject) {
        assertThat(FakeSmtpMailServerFeature.server.getReceivedEmails().size()).isGreaterThanOrEqualTo(index);
        SmtpMessage mailReceived = getIndexMessage(index);
        List<String> headerSubject = mailReceived.getHeaderValues("Subject");
        assertThat(headerSubject.get(0)).isEqualTo(subject);
    }

}
