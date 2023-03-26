package net.nemerosa.ontrack.extension.hook

fun hookDisabled(hook: String) = HookResponse(
    type = HookResponseType.IGNORED,
    info = "Hook `$hook` is disabled."
)

fun hookProcessing(info: Any? = null) = HookResponse(
    type = HookResponseType.PROCESSING,
    info = info,
)
