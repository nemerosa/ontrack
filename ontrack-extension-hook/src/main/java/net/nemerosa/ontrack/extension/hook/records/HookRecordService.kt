package net.nemerosa.ontrack.extension.hook.records

import net.nemerosa.ontrack.extension.hook.HookRequest
import net.nemerosa.ontrack.extension.hook.HookResponse

interface HookRecordService {

    fun onReceived(hook: String, request: HookRequest): String
    fun onUndefined(recordId: String)
    fun onDisabled(recordId: String)
    fun onDenied(recordId: String)
    fun onSuccess(recordId: String, result: HookResponse)
    fun onError(recordId: String, exception: Exception)

}