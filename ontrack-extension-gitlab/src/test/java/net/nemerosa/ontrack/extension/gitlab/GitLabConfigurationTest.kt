package net.nemerosa.ontrack.extension.gitlab

import com.fasterxml.jackson.core.JsonProcessingException
import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GitLabConfigurationTest {
    @Test
    fun toJson() {
        TestUtils.assertJsonWrite(
            mapOf(
                "name" to "ontrack",
                "url" to "https://gitlab.nemerosa.net",
                "user" to "test",
                "password" to "1234567890abcdef",
                "ignoreSslCertificate" to false,
            ).asJson(),
            configurationFixture()
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun fromJson() {
        TestUtils.assertJsonRead<GitLabConfiguration?>(
            configurationFixture(),
            mapOf(
                "name" to "ontrack",
                "url" to "https://gitlab.nemerosa.net",
                "user" to "test",
                "password" to "1234567890abcdef",
                "ignoreSslCertificate" to false,
            ).asJson(),
            GitLabConfiguration::class.java
        )
    }

    @Test
    fun obfuscate() {
        val obfuscate = configurationFixture().obfuscate()
        assertEquals("", obfuscate.password)
    }

    @Test
    fun withPassword() {
        val xxx = configurationFixture().withPassword("xxx")
        assertEquals("xxx", xxx.password)
    }

    private fun configurationFixture(): GitLabConfiguration {
        return GitLabConfiguration(
            "ontrack",
            "https://gitlab.nemerosa.net",
            "test",
            "1234567890abcdef",
            false
        )
    }
}