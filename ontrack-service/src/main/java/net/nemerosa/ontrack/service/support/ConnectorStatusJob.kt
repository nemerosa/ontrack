package net.nemerosa.ontrack.service.support

import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier
import net.nemerosa.ontrack.model.support.ConnectorStatusIndicator
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Stream

@Component
class ConnectorStatusJob(
        private val connectorStatusIndicators: List<ConnectorStatusIndicator>
) : JobOrchestratorSupplier {

    val statuses = ConcurrentHashMap<String, List<CollectedConnectorStatus>>()

    companion object {
        val CONNECTOR_STATUS_JOB = JobCategory.CORE.getType("connector-status").withName("Connector status collection")
    }

    override fun collectJobRegistrations(): Stream<JobRegistration> =
            connectorStatusIndicators.map {
                createJob(it)
            }.map {
                JobRegistration.of(it).withSchedule(Schedule.everyMinutes(5))
            }.stream()

    private fun createJob(connectorStatusIndicator: ConnectorStatusIndicator) = object : Job {

        override fun getKey(): JobKey =
                CONNECTOR_STATUS_JOB.getKey(connectorStatusIndicator.type)

        override fun getTask() = JobRun {
            statuses[connectorStatusIndicator.type] = connectorStatusIndicator.statuses.map { it.collected() }
        }

        override fun getDescription(): String =
                "Collection of connector statuses for type ${connectorStatusIndicator.type}"

        override fun isDisabled(): Boolean = false

    }

}