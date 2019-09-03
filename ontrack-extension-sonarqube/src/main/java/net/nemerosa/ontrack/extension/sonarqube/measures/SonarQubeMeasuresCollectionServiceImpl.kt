package net.nemerosa.ontrack.extension.sonarqube.measures

import net.nemerosa.ontrack.extension.sonarqube.client.SonarQubeClientFactory
import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubeProperty
import net.nemerosa.ontrack.model.metrics.MetricsExportService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.BuildDisplayNameService
import net.nemerosa.ontrack.model.structure.EntityDataService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SonarQubeMeasuresCollectionServiceImpl(
        private val clientFactory: SonarQubeClientFactory,
        private val entityDataService: EntityDataService,
        private val buildDisplayNameService: BuildDisplayNameService,
        private val metricsExportService: MetricsExportService
) : SonarQubeMeasuresCollectionService {

    override fun collect(build: Build, property: SonarQubeProperty) {
        // Client
        val client = clientFactory.getClient(property.configuration)
        // Name of the build
        val version: String = buildDisplayNameService.getBuildDisplayName(build)
        // List of metrics to collect
        // TODO Configurable list of metrics
        val metrics: List<String> = listOf("coverage", "branch_coverage")
        // Getting the measures
        val measures: Map<String, Double?>? = client.getMeasuresForVersion(property.key, version, metrics)
        // TODO Metrics for conversion issues
        // Safe measures
        if (measures != null) {
            val safeMeasures = mutableMapOf<String, Double>()
            measures.forEach { (name, value) ->
                if (value != null) {
                    safeMeasures[name] = value
                }
            }
            // Metrics
            safeMeasures.forEach { (name, value) ->
                metricsExportService.exportMetrics(
                        "ontrack_sonarqube_measure",
                        tags = mapOf(
                                "project" to build.project.name,
                                "branch" to build.branch.name,
                                "build" to build.name,
                                "version" to version,
                                "metric" to name
                        ),
                        fields = mapOf(
                                "value" to value
                        )
                )
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