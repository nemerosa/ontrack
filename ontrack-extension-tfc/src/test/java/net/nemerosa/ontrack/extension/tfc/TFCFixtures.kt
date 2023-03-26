package net.nemerosa.ontrack.extension.tfc

import net.nemerosa.ontrack.extension.tfc.hook.TFCHookPayload
import net.nemerosa.ontrack.extension.tfc.hook.TFCHookPayloadNotification

object TFCFixtures {

    fun hookPayload(
        runId: String = "run-6nGLT9zSLchkCV4A",
        workspaceName: String = "workspace-name",
        organizationName: String = "org-name",
    ) = TFCHookPayload(
        notificationConfigurationId = "any",
        runUrl = "https://app.terraform.io/app/$organizationName/$workspaceName/runs/$runId",
        runId = runId,
        workspaceId = "any",
        workspaceName = workspaceName,
        organizationName = organizationName,
        notifications = listOf(
            TFCHookPayloadNotification(
                message = "any",
                trigger = "run:completed",
                runStatus = "applied",
            )
        )
    )

    // See https://cryptii.com/pipes/hmac
    const val signature = "xxxx"

    // See https://onlinehextools.com/convert-ascii-to-hex
    const val token = "12345678"

}