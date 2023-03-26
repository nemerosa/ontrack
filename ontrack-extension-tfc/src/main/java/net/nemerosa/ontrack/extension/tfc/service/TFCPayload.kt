package net.nemerosa.ontrack.extension.tfc.service

import net.nemerosa.ontrack.extension.tfc.hook.model.TFCHookPayload

data class TFCPayload(
    val parameters: TFCParameters,
    val hookPayload: TFCHookPayload,
)