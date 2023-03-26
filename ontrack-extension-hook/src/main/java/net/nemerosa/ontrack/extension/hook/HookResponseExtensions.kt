package net.nemerosa.ontrack.extension.hook

fun hookDisabled(hook: String) = HookResponse(
    type = HookResponseType.IGNORED,
    info = "Hook `$hook` is disabled."
)

fun hookProcessing(id: String) = HookResponse(
    type = HookResponseType.PROCESSING,
    info = mapOf("id" to id)
)
