package net.nemerosa.ontrack.extension.svn.model;

import org.junit.Test;

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

    private static SVNConfiguration configuration() {
        return new SVNConfiguration(
                "Name",
                "http://host/repository",
                "user",
                "secret",
                "",
                "",
                "",
                "http://browser/file/{path}",
                "http://browser/revision/{revision}",
                "http://browser/file/{path}/{revision}",
                0,
                1,
                ""
        );
    }

}
