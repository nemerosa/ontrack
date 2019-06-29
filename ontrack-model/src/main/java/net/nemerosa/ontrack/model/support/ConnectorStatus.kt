package net.nemerosa.ontrack.model.support

/**
 * Status of a connector.
 */
data class ConnectorStatus(
        val connector: ConnectorDescription,
        val type: ConnectorStatusType,
        val error: String?
) {
    companion object {

        fun ok(connector: ConnectorDescription) = ConnectorStatus(
                connector = connector,
                type = ConnectorStatusType.UP,
                error = null
        )

        fun error(connector: ConnectorDescription, ex: Exception) = ConnectorStatus(
                connector = connector,
                type = ConnectorStatusType.DOWN,
                error = ex.message
        )

    }
}