package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.format
import org.springframework.stereotype.Component

@Component
class HookTestSupport(
    private val hookController: HookController,
) {

    fun hook(
        hook: String,
        body: Any,
        parameters: Map<String, String>,
        headers: Map<String, String>,
    ) = hookController.hook(hook, body.asJson().format(), parameters, headers)

}