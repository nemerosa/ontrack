package net.nemerosa.ontrack.extension.notifications.core

import net.nemerosa.ontrack.common.api.APIDescription

data class YontrackPromotionNotificationChannelOutput(
    @APIDescription("ID of the promotion run")
    val runId: Int,
)
