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
                "", ""
        );
        assertFalse(configuration.isCloud());
    }

    @Test
    public void bitbucketCloud() {
        StashConfiguration configuration = new StashConfiguration(
                "cloud",
                "https://bitbucket.org",
                "", ""
        );
        assertTrue(configuration.isCloud());
    }

}
