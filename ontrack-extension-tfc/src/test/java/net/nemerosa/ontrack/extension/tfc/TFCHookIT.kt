package net.nemerosa.ontrack.extension.tfc

import net.nemerosa.ontrack.extension.hook.HookResponseType
import net.nemerosa.ontrack.extension.hook.HookTestSupport
import net.nemerosa.ontrack.extension.queue.QueueTestSupport
import net.nemerosa.ontrack.extension.tfc.settings.TFCSettings
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.it.SecurityTestSupport
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

@AsAdminTest
class TFCHookIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var tfcTestSupport: TFCTestSupport

    @Autowired
    private lateinit var queueTestSupport: QueueTestSupport

    @Autowired
    private lateinit var hookTestSupport: HookTestSupport

    @Autowired
    private lateinit var securityTestSupport: SecurityTestSupport

    @BeforeEach
    fun token() {
        settingsManagerService.saveSettings(
            TFCSettings(
                enabled = true,
                token = securityTestSupport.provisionToken(),
            )
        )
    }

    @Test
    fun `Validation without variables`() {
        project {
            branch {
                val vs = validationStamp()
                build {
                    queueTestSupport.withSyncQueuing {
                        tfcTestSupport.forTesting {
                            val response = hookTestSupport.hook(
                                hook = "tfc",
                                body = TFCFixtures.hookPayload(),
                                parameters = mapOf(
                                    "project" to project.name,
                                    "branch" to branch.name,
                                    "build" to name,
                                    "validation" to vs.name,
                                ),
                                headers = emptyMap(),
                            )
                            // Processed, because running in sync mode
                            assertEquals(HookResponseType.PROCESSED, response.type)
                            // Checks the build has been validated
                            assertValidated(this, vs)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Verification hook`() {
        tfcTestSupport.forTesting {
            queueTestSupport.withSyncQueuing {
                val response = hookTestSupport.hook(
                    hook = "tfc",
                    body = TFCFixtures.hookPayload(
                        trigger = "verification",
                    ),
                    parameters = mapOf(
                        "project" to "project",
                        "branch" to "branch",
                        "build" to "build",
                        "validation" to "validation",
                    ),
                    headers = emptyMap(),
                )
                assertEquals(HookResponseType.PROCESSED, response.type)
            }
        }
    }

}