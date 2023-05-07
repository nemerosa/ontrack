package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIIgnore

@APIDescription("Request received by a hook")
data class HookRequest(
        @APIDescription("Body of the request")
        val body: String,
        @APIDescription("URL query parameters")
        val parameters: Map<String, String>,
        @APIDescription("Request HTTP headers")
        val headers: Map<String, String>,
) {
    fun getRequiredHeader(name: String): String =
            headers[name]
                    ?: headers[name.lowercase()]
                    ?: throw HookHeaderRequiredException(name)

    fun obfuscate() = HookRequest(
            body = body,
            parameters = parameters,
            headers = emptyMap(),
    )
}
