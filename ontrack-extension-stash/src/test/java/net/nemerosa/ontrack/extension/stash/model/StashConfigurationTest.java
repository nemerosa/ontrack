package net.nemerosa.ontrack.extension.stash.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StashConfigurationTest {

    @Test
    public void obfuscation() {
        StashConfiguration configuration = new StashConfiguration(
                "server",
                "http://stash.mycompany.com",
                "user", "secret", null, null
        );
        assertEquals("", configuration.obfuscate().getPassword());
    }

}
