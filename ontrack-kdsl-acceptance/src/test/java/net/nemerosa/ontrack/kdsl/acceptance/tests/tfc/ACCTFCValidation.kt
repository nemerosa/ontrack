package net.nemerosa.ontrack.kdsl.acceptance.tests.tfc

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.queue.QueueACCTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.resourceAsText
import net.nemerosa.ontrack.kdsl.connector.parse
import net.nemerosa.ontrack.kdsl.spec.Build
import net.nemerosa.ontrack.kdsl.spec.extension.queue.QueueRecordState
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ACCTFCValidation : AbstractACCDSLTestSupport() {

    @Test
    fun `Validation without variables`() {
        project {
            branch {
                val vs = validationStamp()
                build {
                    // Payload
                    val payload =
                        resourceAsText("/tfc/run-completed-applied.json")
                            .parseAsJson()
                    // Sending the payload to the hook
                    val response = sendPayloadToHook(
                        ref = this,
                        validation = vs.name,
                        payload = payload,
                    )
                    // Gets the queue ID
                    val queueID = response.queueID
                    // Waiting for the processing to be don
                    val queueSupport = QueueACCTestSupport(ontrack)
                    queueSupport.waitForQueueRecordToBe(queueID, QueueRecordState.COMPLETED)
                    // Checks the build has been validated to "passed"
                    val run = getValidationRuns(vs.name, 1).firstOrNull()
                    assertNotNull(run, "Validation was done") {
                        assertEquals("PASSED", it.lastStatus.id)
                    }
                }
            }
        }
    }

    private fun sendPayloadToHook(
        ref: Build,
        project: String = ref.branch.project.name,
        branch: String = ref.branch.name,
        build: String = ref.name,
        validation: String,
        payload: JsonNode,
    ): HookResponse {
        val response = rawConnector().post(
            "/hook/secured/tfc?project=$project&branch=$branch&build=$build&validation=$validation",
            headers = mapOf(
                "X-TFE-Notification-Signature" to "signature-is-not-checked",
            ),
            body = payload,
        )
        // Payload: response checks
        assertEquals(200, response.statusCode)
        // Extracting the response
        return response.body.parse()
    }

}