package net.nemerosa.ontrack.extension.hook

fun hookDisabled(hook: String) = HookResponse(
    type = HookResponseType.IGNORED,
    info = "Hook `$hook` is disabled."
)

fun hookConsolidate(
    types: List<HookResponseType>,
    info: Any? = null,
) = HookResponse(
    type = if (types.all { it == HookResponseType.PROCESSED }) {
        HookResponseType.PROCESSED
    } else if (types.all { it == HookResponseType.IGNORED }) {
        HookResponseType.IGNORED
    } else {
        HookResponseType.PROCESSING
    },
    info = info,
)
