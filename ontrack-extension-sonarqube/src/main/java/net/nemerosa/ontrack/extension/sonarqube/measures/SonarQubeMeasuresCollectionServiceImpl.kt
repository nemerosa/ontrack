package net.nemerosa.ontrack.extension.sonarqube.measures

import net.nemerosa.ontrack.extension.sonarqube.client.SonarQubeClientFactory
import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubeProperty
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.EntityDataService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SonarQubeMeasuresCollectionServiceImpl(
        private val clientFactory: SonarQubeClientFactory,
        private val entityDataService: EntityDataService
) : SonarQubeMeasuresCollectionService {

    override fun collect(build: Build, property: SonarQubeProperty) {
        // Client
        val client = clientFactory.getClient(property.configuration)
        // TODO Name of the build
        val version: String = build.name
        // List of metrics to collect
        // TODO Configurable list of metrics
        val metrics: List<String> = listOf("coverage", "branch_coverage")
        // Getting the measures
        val measures: Map<String, Double?>? = client.getMeasuresForVersion(property.key, version, metrics)
        // TODO Metrics
        // TODO Metrics for conversion issues
        // Safe measures
        if (measures != null) {
            val safeMeasures = mutableMapOf<String, Double>()
            measures.forEach { (name, value) ->
                if (value != null) {
                    safeMeasures[name] = value
                }
            }
            // Storage of metrics for build
            entityDataService.store(
                    build,
                    SonarQubeMeasures::class.java.name,
                    SonarQubeMeasures(safeMeasures)
            )
        }
    }

}