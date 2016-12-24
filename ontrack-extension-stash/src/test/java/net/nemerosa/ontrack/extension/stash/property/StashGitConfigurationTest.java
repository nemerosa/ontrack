package net.nemerosa.ontrack.extension.stash.property;

import net.nemerosa.ontrack.extension.stash.model.StashConfiguration;
import org.junit.Test;

import static org.junit.Assert.*;

public class StashGitConfigurationTest {

    @Test
    public void bitbucketServer() {
        StashGitConfiguration gitConfiguration = new StashGitConfiguration(
                new StashProjectConfigurationProperty(
                        new StashConfiguration(
                                "server",
                                "http://stash.mycompany.com",
                                "", ""
                        ), "nemerosa", "ontrack",
                        0, ""
                ),
                null);
        assertFalse(gitConfiguration.isCloud());
        assertEquals("http://stash.mycompany.com/projects/nemerosa/repos/ontrack/commits/{commit}", gitConfiguration.getCommitLink());
        assertEquals("http://stash.mycompany.com/projects/nemerosa/repos/ontrack/browse/{path}?at={commit}", gitConfiguration.getFileAtCommitLink());
        assertEquals("http://stash.mycompany.com/scm/nemerosa/ontrack.git", gitConfiguration.getRemote());
    }

    @Test
    public void bitbucketCloud() {
        StashGitConfiguration gitConfiguration = new StashGitConfiguration(
                new StashProjectConfigurationProperty(
                        new StashConfiguration(
                                "cloud",
                                "https://bitbucket.org",
                                "", ""
                        ), "nemerosa", "ontrack",
                        0, ""
                ),
                null);
        assertTrue(gitConfiguration.isCloud());
        assertEquals("https://bitbucket.org/nemerosa/ontrack/commits/{commit}", gitConfiguration.getCommitLink());
        assertEquals("https://bitbucket.org/nemerosa/ontrack/src/{commit}/{path}", gitConfiguration.getFileAtCommitLink());
        assertEquals("https://bitbucket.org/nemerosa/ontrack.git", gitConfiguration.getRemote());
    }
}
