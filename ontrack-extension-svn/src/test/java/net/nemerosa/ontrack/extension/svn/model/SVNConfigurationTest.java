package net.nemerosa.ontrack.extension.svn.model;

import org.junit.Test;

import static net.nemerosa.ontrack.extension.svn.model.SVNTestFixtures.configuration;
import static org.junit.Assert.assertEquals;

public class SVNConfigurationTest {

    @Test
    public void obfuscate() {
        assertEquals("secret", configuration().getPassword());
        assertEquals("", configuration().obfuscate().getPassword());
    }

    @Test
    public void getRevisionBrowsingURL() {
        SVNConfiguration configuration = configuration();
        assertEquals("http://browser/revision/123", configuration.getRevisionBrowsingURL(123));
    }

}
