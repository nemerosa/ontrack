package net.nemerosa.ontrack.extension.sonarqube.measures

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.sonarqube.client.SonarQubeClientFactory
import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubeProperty
import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubePropertyType
import net.nemerosa.ontrack.model.metrics.MetricsExportService
import net.nemerosa.ontrack.model.metrics.measure
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SonarQubeMeasuresCollectionServiceImpl(
        private val clientFactory: SonarQubeClientFactory,
        private val entityDataService: EntityDataService,
        private val buildDisplayNameService: BuildDisplayNameService,
        private val metricsExportService: MetricsExportService,
        private val meterRegistry: MeterRegistry,
        private val structureService: StructureService,
        private val propertyService: PropertyService,
        private val branchModelMatcherService: BranchModelMatcherService
) : SonarQubeMeasuresCollectionService {

    override fun collect(project: Project, logger: (String) -> Unit) {
        // Gets the SonarQube property of the project
        val property = propertyService.getProperty(project, SonarQubePropertyType::class.java).value
        if (property != null) {
            // Gets the model match for the branch
            val branchModelMatcher = branchModelMatcherService.getBranchModelMatcher(project)
            // For all project branches
            structureService.getBranchesForProject(project.id)
                    // ... filter branches according to the model matcher
                    .filter { branch -> branchModelMatcher?.matches(branch) ?: true }
                    // ... TODO or a filter as property level
                    // ... scans the branch
                    .forEach { collect(it, property, logger) }
        }
    }

    private fun collect(branch: Branch, property: SonarQubeProperty, logger: (String) -> Unit) {
        // Logging
        logger("Getting SonarQube measures for ${branch.entityDisplayName}")
        // Loops over all builds and launch the collection
        structureService.forEachBuild(branch, BuildSortDirection.FROM_NEWEST) { build ->
            // Logging
            logger("Getting SonarQube measures for ${build.entityDisplayName}")
            // Processing
            collect(build, property)
            // Going on
            true
        }
    }

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