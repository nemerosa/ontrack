package net.nemerosa.ontrack.extension.jenkins.postprocessing

import net.nemerosa.ontrack.extension.jenkins.autoversioning.JenkinsPostProcessingConfigCredentials
import net.nemerosa.ontrack.extension.jenkins.autoversioning.JenkinsPostProcessingConfigCredentialsParseException
import net.nemerosa.ontrack.extension.jenkins.autoversioning.JenkinsPostProcessingConfigCredentialsType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class JenkinsPostProcessingConfigCredentialsTest {

    @Test
    fun `Multiline parsing`() {
        val text = """
            usernamePassword,nexus-gradle,NEXUS_USR,NEXUS_PSW
            string,SLACK_TOKEN,SLACK_TOKEN
        """.trimIndent()
        val creds = JenkinsPostProcessingConfigCredentials.parseLines(text)
        assertEquals(
            listOf(
                JenkinsPostProcessingConfigCredentials(
                    JenkinsPostProcessingConfigCredentialsType.USERNAME_PASSWORD,
                    "nexus-gradle",
                    listOf("NEXUS_USR", "NEXUS_PSW")
                ),
                JenkinsPostProcessingConfigCredentials(
                    JenkinsPostProcessingConfigCredentialsType.STRING,
                    "SLACK_TOKEN",
                    listOf("SLACK_TOKEN")
                )
            ),
            creds
        )
    }

    @Test
    fun `Multiline parsing with error`() {
        val text = """
            usernamePassword,nexus-gradle,NEXUS_USR,NEXUS_PSW
            string,SLACK_TOKEN
        """.trimIndent()
        val ex = assertFailsWith<JenkinsPostProcessingConfigCredentialsParseException> {
            JenkinsPostProcessingConfigCredentials.parseLines(text)
        }
        assertEquals(
            "string,SLACK_TOKEN parsing error: Line must contains at least 3 tokens separated by commas.",
            ex.message
        )
    }

    @Test
    fun `Username password parsing`() {
        val text = "usernamePassword,nexus-gradle,NEXUS_USR,NEXUS_PSW"
        val cred = JenkinsPostProcessingConfigCredentials.parseLines(text).first()
        assertEquals(
            JenkinsPostProcessingConfigCredentials(
                JenkinsPostProcessingConfigCredentialsType.USERNAME_PASSWORD,
                "nexus-gradle",
                listOf("NEXUS_USR", "NEXUS_PSW")
            ),
            cred
        )
    }

    @Test
    fun `Username password parsing with white spaces`() {
        val text = "  usernamePassword,  nexus-gradle , NEXUS_USR  , NEXUS_PSW   "
        val cred = JenkinsPostProcessingConfigCredentials.parseLines(text).first()
        assertEquals(
            JenkinsPostProcessingConfigCredentials(
                JenkinsPostProcessingConfigCredentialsType.USERNAME_PASSWORD,
                "nexus-gradle",
                listOf("NEXUS_USR", "NEXUS_PSW")
            ),
            cred
        )
    }

    @Test
    fun `Username colon password parsing`() {
        val text = "usernameColonPassword,nexus-gradle,NEXUS"
        val cred = JenkinsPostProcessingConfigCredentials.parseLines(text).first()
        assertEquals(
            JenkinsPostProcessingConfigCredentials(
                JenkinsPostProcessingConfigCredentialsType.USERNAME_COLON_PASSWORD,
                "nexus-gradle",
                listOf("NEXUS")
            ),
            cred
        )
    }

    @Test
    fun `Token parsing`() {
        val text = "string,SLACK_TOKEN,TOKEN"
        val cred = JenkinsPostProcessingConfigCredentials.parseLines(text).first()
        assertEquals(
            JenkinsPostProcessingConfigCredentials(
                JenkinsPostProcessingConfigCredentialsType.STRING,
                "SLACK_TOKEN",
                listOf("TOKEN")
            ),
            cred
        )
    }

    @Test
    fun `Token not enough parameters`() {
        val text = "string,SLACK_TOKEN"
        assertFailsWith<JenkinsPostProcessingConfigCredentialsParseException> {
            JenkinsPostProcessingConfigCredentials.parseLines(text)
        }.apply {
            assertEquals(
                "string,SLACK_TOKEN parsing error: Line must contains at least 3 tokens separated by commas.",
                message
            )
        }
    }

    @Test
    fun `Username password with not enough parameters`() {
        val text = "usernamePassword,nexus-gradle,NEXUS"
        assertFailsWith<JenkinsPostProcessingConfigCredentialsParseException> {
            JenkinsPostProcessingConfigCredentials.parseLines(text)
        }.apply {
            assertEquals(
                "usernamePassword,nexus-gradle,NEXUS parsing error: usernamePassword variables are not correct: Requires 2 bound variables",
                message
            )
        }
    }

    @Test
    fun `Token too many parameters`() {
        val text = "string,SLACK_TOKEN,TOKEN_USR,TOKEN_PWD"
        assertFailsWith<JenkinsPostProcessingConfigCredentialsParseException> {
            JenkinsPostProcessingConfigCredentials.parseLines(text)
        }.apply {
            assertEquals(
                "string,SLACK_TOKEN,TOKEN_USR,TOKEN_PWD parsing error: string variables are not correct: Requires 1 bound variable",
                message
            )
        }
    }

    @Test
    fun `Unknown type`() {
        val text = "token,SLACK_TOKEN,TOKEN"
        assertFailsWith<JenkinsPostProcessingConfigCredentialsParseException> {
            JenkinsPostProcessingConfigCredentials.parseLines(text)
        }.apply {
            assertEquals("token,SLACK_TOKEN,TOKEN parsing error: token is not a supported type of credentials", message)
        }
    }

}