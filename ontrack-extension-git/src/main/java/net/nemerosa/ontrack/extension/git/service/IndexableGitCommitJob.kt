package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.git.GitRepositoryClient
import net.nemerosa.ontrack.git.GitRepositoryClientFactory
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import java.util.function.Supplier
import java.util.stream.Stream
import kotlin.streams.asStream

/**
 * Manual job to collect Git commits for all builds of a project.
 */
@Component
class IndexableGitCommitJob(
        private val securityService: SecurityService,
        private val structureService: StructureService,
        private val gitService: GitService,
        private val gitRepositoryClientFactory: GitRepositoryClientFactory
) : JobOrchestratorSupplier {

    override fun collectJobRegistrations(): Stream<JobRegistration> {
        return securityService.asAdmin(Supplier {
            structureService
                    .projectList
                    .asSequence()
                    .filter { project ->
                        gitService.getProjectConfiguration(project) != null
                    }
                    .flatMap { project ->
                        sequenceOf(
                                createIndexableGitCommitJobRegistration(project, overrides = true, schedule = Schedule.NONE),
                                createIndexableGitCommitJobRegistration(project, overrides = false, schedule = Schedule.EVERY_DAY)
                        )
                    }
                    .asStream()
        })
    }

    private fun createIndexableGitCommitJobRegistration(project: Project, overrides: Boolean, schedule: Schedule): JobRegistration =
            JobRegistration.of(
                    object : Job {
                        override fun getKey(): JobKey = if (overrides) {
                            GIT_COMMIT_REINDEX_JOB.getKey(project.name)
                        } else {
                            GIT_COMMIT_INDEX_JOB.getKey(project.name)
                        }

                        override fun getTask() = JobRun { listener ->
                            collectIndexableGitCommitForProject(project, overrides, listener)
                        }

                        override fun getDescription(): String = if (overrides) {
                            "Re-indexation of Git commits for project ${project.name}"
                        } else {
                            "Incremental indexation of Git commits for project ${project.name}"
                        }

                        override fun isDisabled(): Boolean = project.isDisabled

                    }
            ).withSchedule(schedule)

    private fun collectIndexableGitCommitForProject(project: Project, overrides: Boolean, listener: JobRunListener) {
        securityService.asAdmin {
            val projectConfiguration = gitService.getProjectConfiguration(project)
            if (projectConfiguration != null) {
                val client: GitRepositoryClient = gitRepositoryClientFactory.getClient(projectConfiguration.gitRepository)
                gitService.forEachConfiguredBranchInProject(project) { branch, config ->
                    gitService.collectIndexableGitCommitForBranch(branch, client, config, overrides, listener)
                }
            }
        }
    }

    companion object {
        private val GIT_COMMIT_REINDEX_JOB = GIT_JOB_CATEGORY.getType("git-commit-reindexation").withName("Git commit re-indexation")
        private val GIT_COMMIT_INDEX_JOB = GIT_JOB_CATEGORY.getType("git-commit-indexation").withName("Git commit indexation")
    }
}