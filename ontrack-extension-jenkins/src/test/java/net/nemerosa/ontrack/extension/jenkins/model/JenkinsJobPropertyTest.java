package net.nemerosa.ontrack.extension.jenkins.model;

import net.nemerosa.ontrack.extension.jenkins.JenkinsConfiguration;
import net.nemerosa.ontrack.extension.jenkins.JenkinsJobProperty;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JenkinsJobPropertyTest {

    private JenkinsJobProperty property;

    @Before
    public void before() {
        JenkinsConfiguration configuration = new JenkinsConfiguration(
                "MyConfig",
                "http://jenkins",
                "user",
                "secret"
        );
        property = new JenkinsJobProperty(
                configuration,
                "MyJob"
        );
    }

    @Test
    public void url() {
        assertEquals("http://jenkins/job/MyJob", property.getUrl());
    }

}
