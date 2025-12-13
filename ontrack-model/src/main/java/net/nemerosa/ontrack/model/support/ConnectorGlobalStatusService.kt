package net.nemerosa.ontrack.model.support

interface ConnectorGlobalStatusService {

    fun collect()

    val globalStatus: ConnectorGlobalStatus

}