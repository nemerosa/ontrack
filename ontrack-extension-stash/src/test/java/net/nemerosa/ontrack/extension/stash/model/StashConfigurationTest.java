package net.nemerosa.ontrack.extension.stash.model;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StashConfigurationTest {

    @Test
    public void bitbucketServer() {
        StashConfiguration configuration = new StashConfiguration(
                "server",
                "http://stash.mycompany.com",
                "", "", 0, ""
        );
        assertFalse(configuration.isCloud());
    }

    @Test
    public void bitbucketCloud() {
        StashConfiguration configuration = new StashConfiguration(
                "cloud",
                "https://bitbucket.org",
                "", "", 0, ""
        );
        assertTrue(configuration.isCloud());
    }

}
