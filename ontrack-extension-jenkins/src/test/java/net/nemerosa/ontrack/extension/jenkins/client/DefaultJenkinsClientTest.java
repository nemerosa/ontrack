package net.nemerosa.ontrack.extension.jenkins.client;

import net.nemerosa.ontrack.client.JsonClient;
import net.nemerosa.ontrack.client.JsonClientImpl;
import net.nemerosa.ontrack.client.OTHttpClient;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultJenkinsClientTest {

    private JenkinsClient jenkinsClient;

    @Before
    public void before() {
        OTHttpClient httpClient = mock(OTHttpClient.class);
        when(httpClient.getUrl(anyString(), any())).thenAnswer(invocation -> {
            String path = (String) invocation.getArguments()[0];
            String parameters = (String) invocation.getArguments()[1];
            return String.format(
                    "http://jenkins/%s",
                    String.format(path, parameters)
            );
        });

        JsonClient jsonClient = new JsonClientImpl(httpClient);

        jenkinsClient = new DefaultJenkinsClient(jsonClient);
    }

    @Test
    public void getSimpleJobUrl() {
        JenkinsJob test = jenkinsClient.getJob("test");
        assertEquals("test", test.getName());
        assertEquals("http://jenkins/job/test", test.getUrl());
    }

    @Test
    public void getFolderJobUrl() {
        JenkinsJob test = jenkinsClient.getJob("test/test-master/test-master-build");
        assertEquals("test-master-build", test.getName());
        assertEquals("http://jenkins/job/test/job/test-master/job/test-master-build", test.getUrl());
    }

    @Test
    public void getCompleteFolderJobUrl() {
        JenkinsJob test = jenkinsClient.getJob("test/job/test-master/job/test-master-build");
        assertEquals("test-master-build", test.getName());
        assertEquals("http://jenkins/job/test/job/test-master/job/test-master-build", test.getUrl());
    }

}
