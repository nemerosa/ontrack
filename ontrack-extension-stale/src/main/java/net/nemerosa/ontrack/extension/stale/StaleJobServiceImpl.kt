package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.stale.StaleBranchStatus.Companion.min
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.job.JobCategory.Companion.of
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.stream.Stream

@Component
class StaleJobServiceImpl(
        extensionManager: ExtensionManager,
        private val structureService: StructureService
) : StaleJobService {

    private val logger: Logger = LoggerFactory.getLogger(StaleJobServiceImpl::class.java)

    private val checks: Set<StaleBranchCheck> by lazy {
        extensionManager.getExtensions(StaleBranchCheck::class.java).toSet()
    }

    override fun collectJobRegistrations(): Stream<JobRegistration> {
        // Gets all projects...
        return structureService.projectList
                // ... which have a StaleProperty
                .filter { project -> isProjectEligible(project) }
                // ... and associates a job with them
                .map { project -> createStaleJob(project) }
                // ... as a stream
                .stream()
    }

    private fun isProjectEligible(project: Project) = checks.any { it.isProjectEligible(project) }

    protected fun createStaleJob(project: Project): JobRegistration = JobRegistration(
            object : Job {
                override fun getKey(): JobKey {
                    return getStaleJobKey(project)
                }

                override fun getTask(): JobRun {
                    return JobRun { runListener: JobRunListener -> detectAndManageStaleBranches(runListener, project) }
                }

                override fun getDescription(): String {
                    return "Detection and management of stale branches for " + project.name
                }

                override fun isDisabled(): Boolean {
                    return project.isDisabled
                }

                override fun isValid(): Boolean = isProjectEligible(project)
            },
            Schedule.EVERY_DAY
    )

    protected fun getStaleJobKey(project: Project): JobKey {
        return STALE_BRANCH_JOB.getKey(project.id.toString())
    }

    override fun detectAndManageStaleBranches(runListener: JobRunListener, project: Project) {
        if (isProjectEligible(project)) {
            structureService.getBranchesForProject(project.id).forEach { branch ->
                // Last build on this branch
                val lastBuild: Build? = structureService.getLastBuild(branch.id).getOrNull()
                detectAndManageStaleBranch(branch, lastBuild)
            }
        }
    }

    fun detectAndManageStaleBranch(branch: Branch, lastBuild: Build?) {
        logger.debug("[{}] Scanning branch for staleness", branch.entityDisplayName)
        // Gets all results
        val status: StaleBranchStatus? = checks.fold<StaleBranchCheck, StaleBranchStatus?>(null) { acc: StaleBranchStatus?, check: StaleBranchCheck ->
            when (acc) {
                null -> check.getBranchStaleness(branch, lastBuild)
                StaleBranchStatus.KEEP -> StaleBranchStatus.KEEP
                else -> min(acc, check.getBranchStaleness(branch, lastBuild))
            }
        }
        // Logging
        logger.debug("[{}] Branch staleness status: {}", branch.entityDisplayName, status)
        // Actions
        when (status) {
            StaleBranchStatus.DELETE -> structureService.deleteBranch(branch.id)
            StaleBranchStatus.DISABLE -> if (!branch.isDisabled) {
                structureService.disableBranch(branch)
            }
            // NUll or KEEP
            else -> {
            }
        }
    }

    companion object {
        val STALE_BRANCH_JOB: JobType = of("cleanup").withName("Cleanup")
                .getType("stale-branches").withName("Stale branches cleanup")
    }
}