package net.nemerosa.ontrack.extension.av.queue

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder

data class AutoVersioningQueuePayload(
    val order: AutoVersioningOrder,
)
