package net.nemerosa.ontrack.model.support

/**
 * Computes the status of a connector.
 */
interface ConnectorStatusIndicator {

    /**
     * ID
     */
    val type: String

    /**
     * Computes a list of statuses
     */
    val statuses: List<ConnectorStatus>

}
