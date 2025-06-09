package net.nemerosa.ontrack.extension.gitlab;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;
import org.junit.jupiter.api.Test;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonRead;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonWrite;
import static org.junit.Assert.assertEquals;

public class GitLabConfigurationTest {

    @Test
    public void toJson() throws JsonProcessingException {
        assertJsonWrite(
                object()
                        .with("name", "ontrack")
                        .with("url", "https://gitlab.nemerosa.net")
                        .with("user", "test")
                        .with("password", "1234567890abcdef")
                        .with("ignoreSslCertificate", false)
                        .end(),
                configurationFixture()
        );
    }

    @Test
    public void fromJson() throws JsonProcessingException {
        assertJsonRead(
                configurationFixture(),
                object()
                        .with("name", "ontrack")
                        .with("url", "https://gitlab.nemerosa.net")
                        .with("user", "test")
                        .with("password", "1234567890abcdef")
                        .with("ignoreSslCertificate", false)
                        .end(),
                GitLabConfiguration.class
        );
    }

    @Test
    public void descriptor() {
        ConfigurationDescriptor descriptor = configurationFixture().getDescriptor();
        assertEquals("ontrack", descriptor.getId());
        assertEquals("ontrack (https://gitlab.nemerosa.net)", descriptor.getName());
    }

    @Test
    public void obfuscate() {
        GitLabConfiguration obfuscate = configurationFixture().obfuscate();
        assertEquals("", obfuscate.getPassword());
    }

    @Test
    public void withPassword() {
        GitLabConfiguration xxx = configurationFixture().withPassword("xxx");
        assertEquals("xxx", xxx.getPassword());
    }

    private GitLabConfiguration configurationFixture() {
        return new GitLabConfiguration(
                "ontrack",
                "https://gitlab.nemerosa.net",
                "test",
                "1234567890abcdef",
                false
        );
    }

}