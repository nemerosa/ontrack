package net.nemerosa.ontrack.model.support

import kotlin.math.roundToInt

class ConnectorGlobalStatus(
        val statuses: List<CollectedConnectorStatus>
) {
    val count = statuses.size

    val upCount = statuses.count { it.status.type == ConnectorStatusType.UP }
    val downCount = statuses.count { it.status.type == ConnectorStatusType.DOWN }

    val status: ConnectorStatusType = if (downCount > 0) ConnectorStatusType.DOWN else ConnectorStatusType.UP

    val percent: Int = if (count > 0) {
        (upCount.toDouble() * 100.0 / count).roundToInt()
    } else {
        100
    }

}