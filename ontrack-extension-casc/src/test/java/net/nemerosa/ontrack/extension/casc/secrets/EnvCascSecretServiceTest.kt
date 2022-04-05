package net.nemerosa.ontrack.extension.casc.secrets

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class EnvCascSecretServiceTest {

    @Test
    fun `Getting the secret from the environment`() {
        val env = mapOf(
            "SECRET_MY_JENKINS_PASSWORD" to "my-password"
        )
        val service = EnvCascSecretService {
            env[it] ?: ""
        }

        assertEquals("my-password", service.getValue("my-jenkins.password"))
        assertEquals("", service.getValue("my-unknown-jenkins.password"))
    }

}