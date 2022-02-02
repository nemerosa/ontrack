package net.nemerosa.ontrack.extension.artifactory.service

import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClientFactory
import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfigurationService
import net.nemerosa.ontrack.extension.artifactory.ArtifactoryConfProperties
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier
import net.nemerosa.ontrack.model.support.ConfigurationServiceListener
import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfiguration
import net.nemerosa.ontrack.extension.artifactory.property.ArtifactoryPromotionSyncProperty
import net.nemerosa.ontrack.extension.artifactory.property.ArtifactoryPromotionSyncPropertyType
import net.nemerosa.ontrack.model.support.AbstractBranchJob
import java.lang.IllegalStateException
import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClient
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.structure.*
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.regex.Pattern
import java.util.stream.Stream

@Service
class ArtifactoryPromotionSyncServiceImpl(
    private val structureService: StructureService,
    private val propertyService: PropertyService,
    private val artifactoryClientFactory: ArtifactoryClientFactory,
    configurationService: ArtifactoryConfigurationService,
    private val artifactoryConfProperties: ArtifactoryConfProperties,
    private val securityService: SecurityService
) : ArtifactoryPromotionSyncService, JobOrchestratorSupplier, ConfigurationServiceListener<ArtifactoryConfiguration> {

    private val logger = LoggerFactory.getLogger(ArtifactoryPromotionSyncServiceImpl::class.java)

    init {
        configurationService.addConfigurationServiceListener(this)
    }

    override fun collectJobRegistrations(): Stream<JobRegistration> {
        return if (artifactoryConfProperties.buildSyncDisabled) {
            Stream.empty()
        } else {
            securityService.asAdmin {
                // For all projects...
                structureService.projectList
                    // ... and their branches
                    .flatMap { project: Project ->
                        structureService.getBranchesForProject(project.id)
                    }
                    // ... gets those with the sync. property
                    .filter { branch: Branch ->
                        propertyService.hasProperty(branch, ArtifactoryPromotionSyncPropertyType::class.java)
                    }
                    // ... creates the job
                    .map { branch: Branch -> scheduleArtifactoryBuildSync(branch) }
            }.stream()
        }
    }

    fun scheduleArtifactoryBuildSync(branch: Branch): JobRegistration {
        val property = propertyService.getProperty(branch, ArtifactoryPromotionSyncPropertyType::class.java).value
        return JobRegistration.of(getBranchSyncJob(branch)).everyMinutes(property.interval.toLong())
    }

    private fun getBranchSyncJobKey(branch: Branch): JobKey {
        return ARTIFACTORY_BUILD_SYNC_JOB.getKey(branch.id.toString())
    }

    private fun getBranchSyncJob(branch: Branch): Job {
        return propertyService.getProperty(branch, ArtifactoryPromotionSyncPropertyType::class.java).value
            ?.let { _: ArtifactoryPromotionSyncProperty ->
                object : AbstractBranchJob(
                    structureService, branch) {
                    override fun getKey(): JobKey =
                        getBranchSyncJobKey(branch)

                    override fun getTask(): JobRun =
                        JobRun { runListener: JobRunListener -> sync(branch, runListener) }

                    override fun getDescription(): String =
                            "Synchronisation of promotions with Artifactory for branch ${branch.project.name}/${branch.name}"

                    override fun isValid(): Boolean =
                        super.isValid() &&
                                propertyService.hasProperty(branch, ArtifactoryPromotionSyncPropertyType::class.java)
                }
            }
            ?: throw IllegalStateException("Branch not configured for Artifactory")
    }

    override fun sync(branch: Branch, listener: JobRunListener) {
        // Gets the sync properties
        val syncProperty = propertyService.getProperty(branch, ArtifactoryPromotionSyncPropertyType::class.java)
        check(!syncProperty.isEmpty) { String.format("Cannot find sync. property on branch %d", branch.id()) }
        val buildName = syncProperty.value.buildName
        val buildNameFilter = syncProperty.value.buildNameFilter
        val configuration: ArtifactoryConfiguration = syncProperty.value.configuration
        val log = "Sync branch ${branch.project.name}/${branch.name} with Artifactory build $buildName ($buildNameFilter)"
        logger.info("[artifactory-sync] {}", log)
        listener.message(log)
        // Build name filter
        val buildNamePattern = Pattern.compile(
            StringUtils.replace(StringUtils.replace(buildNameFilter, ".", "\\."), "*", ".*")
        )
        // Gets an Artifactory client
        val client = artifactoryClientFactory.getClient(configuration)
        // Gets all the build numbers for the specified build name
        val buildNumbers = client.getBuildNumbers(buildName)
            // ... and filter them
            .filter { name: String -> buildNamePattern.matcher(name).matches() }
        // Synchronises the promotion levels for each build
        for (buildNumber in buildNumbers) {
            syncBuild(branch, buildName, buildNumber, client, listener)
        }
    }

    override fun syncBuild(
        branch: Branch,
        artifactoryBuildName: String,
        buildName: String,
        client: ArtifactoryClient,
        listener: JobRunListener
    ) {
        // Looks for the build
        val buildOpt = structureService.findBuildByName(
            branch.project.name,
            branch.name,
            buildName
        )
        if (buildOpt.isPresent) {
            // Log
            val log = String.format("Sync branch %s/%s for Artifactory build %s",
                branch.project.name,
                branch.name,
                buildName)
            logger.debug("[artifactory-sync] {}", log)
            listener.message(log)
            // Gets the build information from Artifactory
            val buildInfo = client.getBuildInfo(artifactoryBuildName, buildName)
            // Gets the list of statuses
            val statuses = client.getStatuses(buildInfo)
            // For all statuses
            for (artifactoryStatus in statuses) {
                val statusName = artifactoryStatus.name
                // Looks for an existing promotion level with the same name on the branch
                val promotionLevelOpt = structureService.findPromotionLevelByName(
                    branch.project.name,
                    branch.name,
                    statusName
                )
                if (promotionLevelOpt.isPresent) {
                    // Looks for an existing promotion run for the build
                    val runOpt = structureService.getLastPromotionRunForBuildAndPromotionLevel(buildOpt.get(),
                        promotionLevelOpt.get())
                    if (!runOpt.isPresent) {
                        // No existing promotion, we can promote safely
                        logger.info("[artifactory-sync] Promote {}/{}/{} to {}",
                            branch.project.name,
                            branch.name,
                            buildName,
                            statusName)
                        // Actual promotion
                        structureService.newPromotionRun(
                            PromotionRun.of(
                                buildOpt.get(),
                                promotionLevelOpt.get(),
                                Signature.of(artifactoryStatus.user).withTime(artifactoryStatus.timestamp),
                                "Promoted from Artifactory"
                            )
                        )
                    }
                }
            }
        }
    }

    companion object {
        private val ARTIFACTORY_BUILD_SYNC_JOB = JobCategory.of("artifactory").withName("Artifactory")
            .getType("build-sync").withName("Artifactory Build synchronisation")
    }
}