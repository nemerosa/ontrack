package net.nemerosa.ontrack.extension.sonarqube.measures

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.sonarqube.client.SonarQubeClientFactory
import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubeProperty
import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubePropertyType
import net.nemerosa.ontrack.model.metrics.MetricsExportService
import net.nemerosa.ontrack.model.metrics.increment
import net.nemerosa.ontrack.model.metrics.measure
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SonarQubeMeasuresCollectionServiceImpl(
        private val clientFactory: SonarQubeClientFactory,
        private val entityDataService: EntityDataService,
        private val buildDisplayNameService: BuildDisplayNameService,
        private val branchDisplayNameService: BranchDisplayNameService,
        private val metricsExportService: MetricsExportService,
        private val meterRegistry: MeterRegistry,
        private val structureService: StructureService,
        private val propertyService: PropertyService,
        private val branchModelMatcherService: BranchModelMatcherService,
        private val cachedSettingsService: CachedSettingsService
) : SonarQubeMeasuresCollectionService {

    override fun collect(project: Project, logger: (String) -> Unit) {
        // Gets the SonarQube property of the project
        val property = propertyService.getProperty(project, SonarQubePropertyType::class.java).value
        if (property != null) {
            // For all project branches
            structureService.getBranchesForProject(project.id)
                    // ... filter branches according to the model matcher
                    .filter { branch -> matches(branch, property) }
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

    override fun matches(build: Build, property: SonarQubeProperty): Boolean {
        return matches(build.branch, property)
    }

    private fun matches(branch: Branch, property: SonarQubeProperty): Boolean {
        val path: String = getBranchPath(branch)
        return matchesPattern(path, property.branchPattern) || (
                property.branchModel && matchesModel(branch)
                )
    }

    private fun matchesModel(branch: Branch): Boolean {
        val matcher = branchModelMatcherService.getBranchModelMatcher(branch.project)
        return matcher?.matches(branch) ?: true
    }

    private fun matchesPattern(path: String, branchPattern: String?): Boolean {
        return branchPattern.isNullOrBlank() || branchPattern.toRegex().matches(path)
    }

    private fun getBranchPath(branch: Branch): String = branchDisplayNameService.getBranchDisplayName(branch)

    override fun collect(build: Build, property: SonarQubeProperty) {
        // Client
        val client = clientFactory.getClient(property.configuration)
        // Name of the build
        val version: String = buildDisplayNameService.getBuildDisplayName(build)
        // List of metrics to collect
        // Configurable list of metrics
        val metrics: List<String> = getListOfMetrics(property)
        // Getting the measures
        val metricTags = mapOf(
                "project" to build.project.name,
                "branch" to build.branch.name,
                "build" to build.name,
                "uri" to property.configuration.url
        )
        val measures: Map<String, Double?>? = meterRegistry.measure(
                started = SonarQubeMetrics.METRIC_ONTRACK_SONARQUBE_COLLECTION_STARTED_COUNT,
                success = SonarQubeMetrics.METRIC_ONTRACK_SONARQUBE_COLLECTION_SUCCESS_COUNT,
                error = SonarQubeMetrics.METRIC_ONTRACK_SONARQUBE_COLLECTION_ERROR_COUNT,
                time = SonarQubeMetrics.METRIC_ONTRACK_SONARQUBE_COLLECTION_TIME,
                tags = metricTags
        ) {
            client.getMeasuresForVersion(property.key, version, metrics)
        }
        // Safe measures
        if (measures != null) {
            val safeMeasures = mutableMapOf<String, Double>()
            measures.forEach { (name, value) ->
                if (value != null) {
                    safeMeasures[name] = value
                } else {
                    // No metric collected
                    meterRegistry.increment(
                            SonarQubeMetrics.METRIC_ONTRACK_SONARQUBE_COLLECTION_NONE_COUNT,
                            *(metricTags + ("measure" to name)).toList().toTypedArray()
                    )
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
                        ),
                        timestamp = build.signature.time
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

    private fun getListOfMetrics(property: SonarQubeProperty): List<String> {
        return if (property.override) {
            property.measures
        } else {
            val settings = cachedSettingsService.getCachedSettings(SonarQubeMeasuresSettings::class.java)
            (settings.measures.toSet() + property.measures).toList()
        }
    }

    override fun getMeasures(build: Build): SonarQubeMeasures? = entityDataService.retrieve(
            build,
            SonarQubeMeasures::class.java.name,
            SonarQubeMeasures::class.java
    )
}