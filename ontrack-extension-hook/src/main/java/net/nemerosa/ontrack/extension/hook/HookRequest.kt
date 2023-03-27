package net.nemerosa.ontrack.extension.hook

data class HookRequest(
    val body: String,
    val parameters: Map<String, String>,
    val headers: Map<String, String>,
) {
    fun getRequiredHeader(name: String): String =
        headers[name]
            ?: headers[name.lowercase()]
            ?: throw HookHeaderRequiredException(name)
}
