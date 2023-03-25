package net.nemerosa.ontrack.extension.tfc.hook.dispatching

import net.nemerosa.ontrack.extension.tfc.hook.TFCHookParameters
import net.nemerosa.ontrack.extension.tfc.hook.TFCHookResponse
import net.nemerosa.ontrack.extension.tfc.hook.model.TFCHookPayload

interface TFCHookDispatcher {

    fun dispatch(parameters: TFCHookParameters, payload: TFCHookPayload): TFCHookResponse

}