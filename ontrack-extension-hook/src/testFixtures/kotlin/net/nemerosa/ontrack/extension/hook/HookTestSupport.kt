package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.extension.hook.records.HookRecord
import net.nemerosa.ontrack.extension.hook.records.HookRecordQueryFilter
import net.nemerosa.ontrack.extension.hook.records.HookRecordingsExtension
import net.nemerosa.ontrack.extension.recordings.RecordingsCleanupService
import net.nemerosa.ontrack.extension.recordings.RecordingsQueryService
import net.nemerosa.ontrack.extension.recordings.RecordingsService
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.format
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Component
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Component
class HookTestSupport(
    private val hookController: HookController,
    private val recordingsQueryService: RecordingsQueryService,
    private val recordingsCleanupService: RecordingsCleanupService,
    private val recordingsService: RecordingsService,
    private val hookRecordingsExtension: HookRecordingsExtension,
    private val testHookEndpointExtension: TestHookEndpointExtension,
    private val securityService: SecurityService,
) {

    fun hook(
        hook: String,
        body: Any,
        parameters: Map<String, String>,
        headers: Map<String, String>,
    ) = hookController.hook(hook, body.asJson().format(), parameters, headers)

    fun testHook(
        enabled: Boolean = true,
        denied: Boolean = false,
        error: Boolean = false,
        body: String = "Any body for now",
        parameters: Map<String, String> = emptyMap(),
        token: String? = null,
    ): HookResponse {
        val oldEnabled = testHookEndpointExtension.enabled
        val oldDenied = testHookEndpointExtension.denied
        val oldError = testHookEndpointExtension.error
        return try {
            testHookEndpointExtension.enabled = enabled
            testHookEndpointExtension.denied = denied
            testHookEndpointExtension.error = error

            testHookEndpointExtension.provisionToken(token)

            hook(
                hook = "test",
                body = body,
                parameters = parameters,
                headers = emptyMap(),
            )
        } finally {
            testHookEndpointExtension.enabled = oldEnabled
            testHookEndpointExtension.denied = oldDenied
            testHookEndpointExtension.error = oldError
        }
    }

    fun assertRecordNotPresent(id: String, message: String) {
        val record = securityService.asAdmin {
            recordingsQueryService.findById(hookRecordingsExtension, id)
        }
        assertNull(record, message)
    }

    fun assertRecordPresent(id: String, message: String) {
        val record = securityService.asAdmin {
            recordingsQueryService.findById(hookRecordingsExtension, id)
        }
        assertNotNull(record, message)
    }

    fun assertLatestHookRecord(
        hook: String,
        code: (HookRecord) -> Unit
    ) {
        val record = securityService.asAdmin {
            recordingsQueryService.findByFilter(
                extension = hookRecordingsExtension,
                filter = HookRecordQueryFilter(hook = hook),
                offset = 0,
                size = 1
            ).pageItems.firstOrNull()
        }
        assertNotNull(record, "Found a hook record with hook = $hook") {
            code(it)
        }
    }

    fun record(record: HookRecord) {
        recordingsService.record(hookRecordingsExtension, record)
    }

    fun clearRecords(hook: String = "test") {
        recordingsCleanupService.cleanup(hookRecordingsExtension)
    }

}