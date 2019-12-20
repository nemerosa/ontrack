package net.nemerosa.ontrack.extension.sonarqube.measures

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.sonarqube.client.SonarQubeClientFactory
import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubeProperty
import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubePropertyType
import net.nemerosa.ontrack.model.metrics.MetricsExportService
import net.nemerosa.ontrack.model.metrics.increment
import net.nemerosa.ontrack.model.metrics.measure
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.callAsAdmin
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.*
import org.slf4j.LoggerFactory
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
        private val cachedSettingsService: CachedSettingsService,
        private val securityService: SecurityService
) : SonarQubeMeasuresCollectionService {

    private val logger = LoggerFactory.getLogger(SonarQubeMeasuresCollectionService::class.java)

    override fun collect(project: Project, logger: (String) -> Unit) {
        // Gets the SonarQube property of the project
        val property = propertyService.getProperty(project, SonarQubePropertyType::class.java).value
        if (property != null) {
            // List of metrics to collect
            // Configurable list of metrics
            val metrics: List<String> = getListOfMetrics(property)
            // For all project branches
            structureService.getBranchesForProject(project.id)
                    // ... filter branches according to the model matcher
                    .filter { branch -> matches(branch, property) }
                    // ... scans the branch
                    .forEach { collect(it, property, metrics, logger) }
        }
    }

    private fun collect(branch: Branch, property: SonarQubeProperty, metrics: List<String>, logger: (String) -> Unit) {
        // Logging
        logger("Getting SonarQube measures for ${branch.entityDisplayName}")
        // Gets the validation stamp for this branch
        val vs = structureService.getValidationStampListForBranch(branch.id)
                .find { it.name == property.validationStamp }
        if (vs != null) {
            // Loops over all builds and launch the collection
            structureService.forEachBuild(branch, BuildSortDirection.FROM_NEWEST) { build ->
                // Logging
                logger("Getting SonarQube measures for ${build.entityDisplayName}")
                // Processing
                doCollect(build, property, metrics, vs)
                // Going on
                true
            }
        }
    }

    override fun matches(build: Build, property: SonarQubeProperty): Boolean {
        return matches(build.branch, property)
    }

    private fun matches(branch: Branch, property: SonarQubeProperty): Boolean {
        val path: String = getBranchPath(branch)
        return matchesPattern(path, property.branchPattern) && (!property.branchModel || matchesModel(branch))
    }

    private fun matchesModel(branch: Branch): Boolean {
        val matcher = branchModelMatcherService.getBranchModelMatcher(branch.project)
        return matcher?.matches(branch) ?: true
    }

    private fun matchesPattern(path: String, branchPattern: String?): Boolean {
        return branchPattern.isNullOrBlank() || branchPattern.toRegex().matches(path)
    }

    private fun getBranchPath(branch: Branch): String = branchDisplayNameService.getBranchDisplayName(branch)

    override fun collect(build: Build, property: SonarQubeProperty): SonarQubeMeasuresCollectionResult {
        // Gets the validation stamp for this branch
        val vs = structureService.getValidationStampListForBranch(build.branch.id)
                .find { it.name == property.validationStamp }
        // Collection for this build, only if there is a validation stamp being defined
        return if (vs != null) {
            val result = doCollect(build, property, getListOfMetrics(property), vs)
            if (logger.isDebugEnabled) {
                logger.debug("build=${build.entityDisplayName},scan=${result.ok},result=${result.message}")
            }
            result
        } else {
            SonarQubeMeasuresCollectionResult.error("Validation stamp ${property.validationStamp} cannot be found in ${build.branch.entityDisplayName}")
        }
    }

    private fun doCollect(build: Build, property: SonarQubeProperty, metrics: List<String>, validationStamp: ValidationStamp): SonarQubeMeasuresCollectionResult {
        // Gets the validation runs for this build and the validation stamp
        val passed = structureService.getValidationRunsForBuildAndValidationStamp(
                buildId = build.id,
                validationStampId = validationStamp.id,
                offset = 0,
                count = 1
        ).any { it.isPassed }
        // If not passed, we don't want to go further
        if (!passed) return SonarQubeMeasuresCollectionResult.error("No passed validation run for ${validationStamp.name} can be found for ${build.entityDisplayName}")
        // Client
        val client = clientFactory.getClient(property.configuration)
        // Name of the build
        val version: String = buildDisplayNameService.getBuildDisplayName(build)
        // Getting the measures
        val metricTags = mapOf(
                "project" to build.project.name,
                "branch" to build.branch.name,
                "uri" to property.configuration.url
        )
        val measures: Map<String, Double?>? = meterRegistry.measure(
                started = SonarQubeMetrics.METRIC_ONTRACK_SONARQUBE_COLLECTION_STARTED_COUNT,
                success = SonarQubeMetrics.METRIC_ONTRACK_SONARQUBE_COLLECTION_SUCCESS_COUNT,
                error = SonarQubeMetrics.METRIC_ONTRACK_SONARQUBE_COLLECTION_ERROR_COUNT,
                time = SonarQubeMetrics.METRIC_ONTRACK_SONARQUBE_COLLECTION_TIME,
                tags = metricTags
        ) {
            val scmBranch = getBranchPath(build.branch)
            client.getMeasuresForVersion(property.key, scmBranch, version, metrics)
        }
        // Safe measures
        if (!measures.isNullOrEmpty()) {
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
                        SonarQubeMetrics.METRIC_ONTRACK_SONARQUBE_MEASURE,
                        tags = mapOf(
                                "project" to build.project.name,
                                "branch" to build.branch.name,
                                "build" to build.name,
                                "version" to version,
                                "measure" to name
                        ),
                        fields = mapOf(
                                "value" to value
                        ),
                        timestamp = build.signature.time
                )
            }
            // Storage of metrics for build
            securityService.callAsAdmin {
                entityDataService.store(
                        build,
                        SonarQubeMeasures::class.java.name,
                        SonarQubeMeasures(safeMeasures)
                )
            }
            // OK
            return SonarQubeMeasuresCollectionResult.ok(safeMeasures)
        } else {
            return SonarQubeMeasuresCollectionResult.error("No SonarQube measure can be found for ${build.entityDisplayName}")
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