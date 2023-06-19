package net.nemerosa.ontrack.extension.github.service

import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.asJsonString
import net.nemerosa.ontrack.model.support.ConfigurationRepository
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GitHubConfigurationTokenMigrationIT : AbstractGitHubTestSupport() {

    @Autowired
    private lateinit var configurationRepository: ConfigurationRepository

    @Autowired
    private lateinit var migration: GitHubConfigurationTokenMigration

    @Test
    fun `Backward compatibility with previously plain tokens`() {
        val config = GitHubEngineConfiguration(
            name = TestUtils.uid("GH"),
            url = null,
            oauth2Token = "xxxxx",
        )
        withDisabledConfigurationTest {
            asAdmin {
                // Saving a configuration using the old format (non encrypted)
                // Not enough to use the repository, we need to access the database directly to save an old format
                namedParameterJdbcTemplate.update(
                    "INSERT INTO CONFIGURATIONS(TYPE, NAME, CONTENT) VALUES (:type, :name, CAST(:content AS JSONB))",
                    mapOf(
                        "name" to config.name,
                        "type" to GitHubEngineConfiguration::class.java.name,
                        "content" to mapOf(
                            "name" to config.name,
                            "url" to config.url,
                            "user" to null,
                            "password" to null,
                            "oauth2Token" to config.oauth2Token,
                        ).asJson().asJsonString()
                    )
                )
                // Checks it's saved in plain
                assertNotNull(configurationRepository.find(GitHubEngineConfiguration::class.java, config.name)) {
                    assertEquals("xxxxx", it.oauth2Token)
                }
                // Running the migration twice and checking it's idempotent
                repeat(3) {
                    // Runs the migration
                    migration.start()
                    // Testing it can be read again
                    gitConfigurationService.getConfiguration(config.name).apply {
                        // Checks its token has been decrypted
                        assertEquals("xxxxx", oauth2Token)
                    }
                    // Checks it's now encrypted in the database
                    assertNotNull(configurationRepository.find(GitHubEngineConfiguration::class.java, config.name)) {
                        assertFalse(it.oauth2Token.isNullOrBlank(), "Token is saved")
                        assertTrue(it.oauth2Token != "xxxxx", "Token is encrypted")
                    }
                }
            }
        }
    }

}