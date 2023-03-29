package net.nemerosa.ontrack.extension.queue.ui

import net.nemerosa.ontrack.model.annotations.APIDescription

data class QueueRecordFilterInfo(
    @APIDescription("List of processors")
    val processors: List<String>,
)