package net.nemerosa.ontrack.extension.jenkins.autoversioning

import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class JenkinsPostProcessingConfigTest {

    @Test
    fun `Parsing when no credentials`() {
        assertEquals(
            JenkinsPostProcessingConfig(
                dockerImage = "image",
                dockerCommand = "command"
            ),
            JenkinsPostProcessingConfig.parseJson(
                mapOf(
                    "dockerImage" to "image",
                    "dockerCommand" to "command"
                ).asJson()
            )
        )
    }

    @Test
    fun `Parsing when empty credentials`() {
        assertEquals(
            JenkinsPostProcessingConfig(
                dockerImage = "image",
                dockerCommand = "command",
                credentials = emptyList(),
            ),
            JenkinsPostProcessingConfig.parseJson(
                mapOf(
                    "dockerImage" to "image",
                    "dockerCommand" to "command",
                    "credentials" to emptyList<String>(),
                ).asJson()
            )
        )
    }

    @Test
    fun `Parsing when expanded credentials`() {
        assertEquals(
            JenkinsPostProcessingConfig(
                dockerImage = "image",
                dockerCommand = "command",
                credentials = listOf(
                    JenkinsPostProcessingConfigCredentials(
                        type = JenkinsPostProcessingConfigCredentialsType.USERNAME_PASSWORD,
                        id = "TEST_CREDENTIALS",
                        vars = listOf("TEST_USR", "TEST_PSW")
                    ),
                    JenkinsPostProcessingConfigCredentials(
                        type = JenkinsPostProcessingConfigCredentialsType.STRING,
                        id = "TEST_TOKEN",
                        vars = listOf("TEST_TOKEN")
                    ),
                ),
            ),
            JenkinsPostProcessingConfig.parseJson(
                mapOf(
                    "dockerImage" to "image",
                    "dockerCommand" to "command",
                    "credentials" to listOf(
                        mapOf(
                            "type" to "USERNAME_PASSWORD",
                            "id" to "TEST_CREDENTIALS",
                            "vars" to listOf("TEST_USR", "TEST_PSW")
                        ),
                        mapOf(
                            "type" to "STRING",
                            "id" to "TEST_TOKEN",
                            "vars" to listOf("TEST_TOKEN")
                        ),
                    ),
                ).asJson()
            )
        )
    }

    @Test
    fun `Parsing when single line short credentials`() {
        assertEquals(
            JenkinsPostProcessingConfig(
                dockerImage = "image",
                dockerCommand = "command",
                credentials = listOf(
                    JenkinsPostProcessingConfigCredentials(
                        type = JenkinsPostProcessingConfigCredentialsType.USERNAME_PASSWORD,
                        id = "TEST_CREDENTIALS",
                        vars = listOf("TEST_USR", "TEST_PSW")
                    ),
                    JenkinsPostProcessingConfigCredentials(
                        type = JenkinsPostProcessingConfigCredentialsType.STRING,
                        id = "TEST_TOKEN",
                        vars = listOf("TEST_TOKEN")
                    ),
                ),
            ),
            JenkinsPostProcessingConfig.parseJson(
                mapOf(
                    "dockerImage" to "image",
                    "dockerCommand" to "command",
                    "credentials" to "usernamePassword,TEST_CREDENTIALS,TEST_USR,TEST_PSW|string,TEST_TOKEN,TEST_TOKEN",
                ).asJson()
            )
        )
    }

    @Test
    fun `Parsing when multi line short credentials`() {
        assertEquals(
            JenkinsPostProcessingConfig(
                dockerImage = "image",
                dockerCommand = "command",
                credentials = listOf(
                    JenkinsPostProcessingConfigCredentials(
                        type = JenkinsPostProcessingConfigCredentialsType.USERNAME_PASSWORD,
                        id = "TEST_CREDENTIALS",
                        vars = listOf("TEST_USR", "TEST_PSW")
                    ),
                    JenkinsPostProcessingConfigCredentials(
                        type = JenkinsPostProcessingConfigCredentialsType.STRING,
                        id = "TEST_TOKEN",
                        vars = listOf("TEST_TOKEN")
                    ),
                ),
            ),
            JenkinsPostProcessingConfig.parseJson(
                mapOf(
                    "dockerImage" to "image",
                    "dockerCommand" to "command",
                    "credentials" to """
                        |usernamePassword,TEST_CREDENTIALS,TEST_USR,TEST_PSW
                        |string,TEST_TOKEN,TEST_TOKEN""".trimMargin(),
                ).asJson()
            )
        )
    }

}