package net.nemerosa.ontrack.extension.sonarqube.measures

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.sonarqube.client.SonarQubeClientFactory
import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubeProperty
import net.nemerosa.ontrack.model.metrics.MetricsExportService
import net.nemerosa.ontrack.model.metrics.measure
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
        private val metricsExportService: MetricsExportService,
        private val meterRegistry: MeterRegistry
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
        val measures: Map<String, Double?>? = meterRegistry.measure(
                started = SonarQubeMetrics.METRIC_ONTRACK_SONARQUBE_COLLECTION_STARTED_COUNT,
                success = SonarQubeMetrics.METRIC_ONTRACK_SONARQUBE_COLLECTION_SUCCESS_COUNT,
                error = SonarQubeMetrics.METRIC_ONTRACK_SONARQUBE_COLLECTION_ERROR_COUNT,
                time = SonarQubeMetrics.METRIC_ONTRACK_SONARQUBE_COLLECTION_TIME,
                tags = mapOf(
                        "project" to build.project.name,
                        "branch" to build.branch.name,
                        "build" to build.name,
                        "uri" to property.configuration.url
                )
        ) {
            client.getMeasuresForVersion(property.key, version, metrics)
        }
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