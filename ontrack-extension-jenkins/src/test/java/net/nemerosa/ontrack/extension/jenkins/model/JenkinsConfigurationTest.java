package net.nemerosa.ontrack.extension.jenkins.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JenkinsConfigurationTest {

    @Test
    public void obfuscate() {
        JenkinsConfiguration config = new JenkinsConfiguration("Test", "http://host", "user", "secret");
        assertEquals("secret", config.getPassword());
        config = config.obfuscate();
        assertEquals("", config.getPassword());
    }

}
