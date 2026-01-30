package net.nemerosa.ontrack.extension.notifications.core

import net.nemerosa.ontrack.common.api.APIDescription

data class OntrackValidationNotificationChannelOutput(
    @APIDescription("ID of the validation run")
    val runId: Int,
)
