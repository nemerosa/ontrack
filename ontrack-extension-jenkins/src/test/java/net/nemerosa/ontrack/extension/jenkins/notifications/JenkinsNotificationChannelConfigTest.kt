package net.nemerosa.ontrack.extension.jenkins.notifications

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JenkinsNotificationChannelConfigTest {

    @Test
    fun `Parsing without parameters`() {
        assertEquals(
            JenkinsNotificationChannelConfig(
                config = "config-name",
                job = "/my/path",
                parameters = emptyList(),
                callMode = JenkinsNotificationChannelConfigCallMode.ASYNC,
                timeout = JenkinsNotificationChannelConfig.DEFAULT_TIMEOUT,
            ),
            mapOf(
                "config" to "config-name",
                "job" to "/my/path"
            ).asJson().parse()
        )
    }

}