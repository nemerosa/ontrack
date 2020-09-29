package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.common.Time.now
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.job.JobCategory.Companion.of
import net.nemerosa.ontrack.model.structure.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*
import java.util.function.Consumer
import java.util.stream.Stream

@Component
class StaleJobServiceImpl(
        private val structureService: StructureService,
        private val propertyService: PropertyService
) : StaleJobService {

    private val logger: Logger = LoggerFactory.getLogger(StaleJobServiceImpl::class.java)

    override fun collectJobRegistrations(): Stream<JobRegistration> {
        // Gets all projects...
        return structureService.projectList
                // ... which have a StaleProperty
                .filter { project -> propertyService.hasProperty(project, StalePropertyType::class.java) }
                // ... and associates a job with them
                .map { project -> createStaleJob(project) }
                // ... as a stream
                .stream()
    }

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

                override fun isValid(): Boolean {
                    return propertyService.hasProperty(project, StalePropertyType::class.java)
                }
            },
            Schedule.EVERY_DAY
    )

    protected fun getStaleJobKey(project: Project): JobKey {
        return STALE_BRANCH_JOB.getKey(project.id.toString())
    }

    protected fun trace(project: Project, pattern: String?, vararg arguments: Any?) {
        logger.debug(String.format(
                "[%s] %s",
                project.name, String.format(pattern!!, *arguments)))
    }

    override fun detectAndManageStaleBranches(runListener: JobRunListener, project: Project) {
        // Gets the stale property for the project
        propertyService.getProperty(project, StalePropertyType::class.java).option().ifPresent { property: StaleProperty ->
            // Disabling and deletion times
            val disablingDuration = property.disablingDuration
            val deletionDuration = property.deletingDuration
            val promotionsToKeep = property.promotionsToKeep
            if (disablingDuration <= 0) {
                trace(project, "No disabling time being set - exiting.")
            } else {
                // Current time
                val now = now()
                // Disabling time
                val disablingTime = now.minusDays(disablingDuration.toLong())
                // Deletion time
                val deletionTime: LocalDateTime? = if (deletionDuration > 0) disablingTime.minusDays(deletionDuration.toLong()) else null
                // Logging
                trace(project, "Disabling time: %s", disablingTime)
                trace(project, "Deletion time: %s", deletionTime)
                // Going on with the scan of the project
                runListener.message("Scanning %s project for stale branches", project.name)
                trace(project, "Scanning project for stale branches")
                structureService.getBranchesForProject(project.id).forEach(
                        Consumer { branch: Branch -> detectAndManageStaleBranch(branch, disablingTime, deletionTime, promotionsToKeep) }
                )
            }
        }
    }

    override fun detectAndManageStaleBranch(branch: Branch, disablingTime: LocalDateTime?, deletionTime: LocalDateTime?, promotionsToKeep: List<String>?) {
        trace(branch.project, "[%s] Scanning branch for staleness", branch.name)
        // Indexation of promotion levels to protect
        val promotionsToProtect: Set<String> = if (promotionsToKeep != null) {
            HashSet(promotionsToKeep)
        } else {
            emptySet()
        }
        // Gets the last promotions for this branch
        val lastPromotions = structureService.getBranchStatusView(branch).promotions
        val isProtected = lastPromotions.stream()
                .anyMatch { promotionView: PromotionView ->
                    (promotionView.promotionRun != null
                            && promotionsToProtect.contains(promotionView.promotionLevel.name))
                }
        if (isProtected) {
            trace(branch.project, "[%s] Branch is promoted and is not eligible for staleness", branch.name)
            return
        }
        // Last date
        val lastTime: LocalDateTime
        // Last build on this branch
        val oBuild = structureService.getLastBuild(branch.id)
        lastTime = if (!oBuild.isPresent) {
            trace(branch.project, "[%s] No available build - taking branch's creation time", branch.name)
            // Takes the branch creation time from the branch itself
            branch.signature.time
        } else {
            val (_, _, _, signature) = oBuild.get()
            signature.time
        }
        // Logging
        trace(branch.project, "[%s] Branch last build activity: %s", branch.name, lastTime)
        // Deletion?
        if (deletionTime != null && deletionTime > lastTime) {
            trace(branch.project, "[%s] Branch due for deletion", branch.name)
            structureService.deleteBranch(branch.id)
        } else if (disablingTime != null && disablingTime > lastTime && !branch.isDisabled) {
            trace(branch.project, "[%s] Branch due for staleness - disabling", branch.name)
            structureService.saveBranch(
                    branch.withDisabled(true)
            )
        } else {
            trace(branch.project, "[%s] Not touching the branch", branch.name)
        }
    }

    companion object {
        val STALE_BRANCH_JOB: JobType = of("cleanup").withName("Cleanup")
                .getType("stale-branches").withName("Stale branches cleanup")
    }
}