package net.nemerosa.ontrack.extension.hook.records

import net.nemerosa.ontrack.common.api.APIDescription

@APIDescription("State a hook request can be in")
enum class HookRecordState {

    RECEIVED,
    UNDEFINED,
    DISABLED,
    DENIED,
    SUCCESS,
    ERROR,

}