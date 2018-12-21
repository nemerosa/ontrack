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
                    .map { project ->
                        createIndexableGitCommitJobRegistration(project)
                    }
                    .asStream()
        })
    }

    private fun createIndexableGitCommitJobRegistration(project: Project): JobRegistration =
            JobRegistration.of(
                    object : Job {
                        override fun getKey(): JobKey =
                                GIT_COMMIT_INDEX_JOB.getKey(project.name)

                        override fun getTask() = JobRun { listener ->
                            collectIndexableGitCommitForProject(project, listener)
                        }

                        override fun getDescription(): String =
                                "Indexation of Git commits for project ${project.name}"

                        override fun isDisabled(): Boolean = project.isDisabled

                    }
            ).withSchedule(Schedule.NONE)

    private fun collectIndexableGitCommitForProject(project: Project, listener: JobRunListener) {
        securityService.asAdmin {
            val projectConfiguration = gitService.getProjectConfiguration(project)
            if (projectConfiguration != null) {
                val client: GitRepositoryClient = gitRepositoryClientFactory.getClient(projectConfiguration.gitRepository)
                gitService.forEachConfiguredBranchInProject(project) { branch, config ->
                    gitService.collectIndexableGitCommitForBranch(branch, client, config, listener)
                }
            }
        }
    }

    companion object {
        private val GIT_COMMIT_INDEX_JOB = GIT_JOB_CATEGORY.getType("git-commit-indexation").withName("Git commit indexation")
    }
}