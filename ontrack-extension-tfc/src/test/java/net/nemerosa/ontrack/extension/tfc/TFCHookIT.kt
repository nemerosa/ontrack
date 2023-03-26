package net.nemerosa.ontrack.extension.tfc

import net.nemerosa.ontrack.extension.hook.HookResponseType
import net.nemerosa.ontrack.extension.hook.HookTestSupport
import net.nemerosa.ontrack.extension.queue.QueueTestSupport
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class TFCHookIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var tfcTestSupport: TFCTestSupport

    @Autowired
    private lateinit var queueTestSupport: QueueTestSupport

    @Autowired
    private lateinit var hookTestSupport: HookTestSupport

    @Test
    fun `Validation without variables`() {
        project {
            branch {
                val vs = validationStamp()
                build {
                    queueTestSupport.withSyncQueuing {
                        tfcTestSupport.withNoSignature {
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
                            TODO()
                        }
                    }
                }
            }
        }
    }

}