package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.extension.hook.records.HookRecord
import net.nemerosa.ontrack.extension.hook.records.HookRecordQueryFilter
import net.nemerosa.ontrack.extension.hook.records.HookRecordQueryService
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.format
import org.springframework.stereotype.Component
import kotlin.test.assertNotNull

@Component
class HookTestSupport(
        private val hookController: HookController,
        private val hookRecordQueryService: HookRecordQueryService,
        private val testHookEndpointExtension: TestHookEndpointExtension,
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
    ): HookResponse {
        val oldEnabled = testHookEndpointExtension.enabled
        val oldDenied = testHookEndpointExtension.denied
        val oldError = testHookEndpointExtension.error
        return try {
            testHookEndpointExtension.enabled = enabled
            testHookEndpointExtension.denied = denied
            testHookEndpointExtension.error = error
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

    fun assertLatestHookRecord(
            hook: String,
            code: (HookRecord) -> Unit
    ) {
        val record = hookRecordQueryService.findByFilter(
                filter = HookRecordQueryFilter(hook = hook),
                offset = 0,
                size = 1
        ).pageItems.firstOrNull()
        assertNotNull(record, "Found a hook record with hook = $hook") {
            code(it)
        }
    }

    fun clearRecords(hook: String = "test") {
        hookRecordQueryService.deleteByFilter(HookRecordQueryFilter(hook = hook))
    }

}