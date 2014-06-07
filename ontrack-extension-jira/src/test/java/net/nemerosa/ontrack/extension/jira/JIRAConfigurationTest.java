package net.nemerosa.ontrack.extension.jira;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JIRAConfigurationTest {

    @Test
    public void obfuscate() {
        JIRAConfiguration config = new JIRAConfiguration("Test", "http://host", "user", "secret");
        assertEquals("secret", config.getPassword());
        config = config.obfuscate();
        assertEquals("", config.getPassword());
    }

}
