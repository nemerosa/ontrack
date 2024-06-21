package net.nemerosa.ontrack.extension.notifications.core

import net.nemerosa.ontrack.model.annotations.APIDescription

data class OntrackValidationNotificationChannelOutput(
    @APIDescription("ID of the validation run")
    val runId: Int,
)
