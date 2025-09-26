package net.nemerosa.ontrack.extension.scm.index

import net.nemerosa.ontrack.extension.scm.SCMJobs
import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.JobProvider
import org.springframework.stereotype.Component

@Component
class SCMBuildCommitIndexJobProvider(
    private val structureService: StructureService,
    private val scmBuildCommitIndexService: SCMBuildCommitIndexService,
    private val scmDetector: SCMDetector,
) : JobProvider {

    override fun getStartingJobs(): Collection<JobRegistration> =
        listOf(
            JobRegistration(
                createSCMBuildCommitIndexJob(),
                Schedule.EVERY_DAY,
            ),
            JobRegistration(
                createSCMBuildCommitIndexCleanupJob(),
                Schedule.NONE,
            ),
        )

    private fun createSCMBuildCommitIndexJob() = object : Job {
        override fun getKey(): JobKey =
            SCMJobs.category
                .getType("build-commit-index").withName("SCM Build Commit Indexes")
                .getKey("indexation")

        override fun getTask() = JobRun { listener ->
            val projects = structureService.projectList
            projects.forEach { project ->
                listener.message("Indexation of SCM build/commit links for ${project.name}...")
                val count = scmBuildCommitIndexService.indexBuildCommits(project)
                if (count < 0) {
                    listener.message("Indexation of SCM build/commit links for ${project.name}: not possible for this project.")
                } else {
                    listener.message("Indexation of SCM build/commit links for ${project.name}: $count commits indexed.")
                }
            }
        }

        override fun getDescription(): String = "SCM Build Commit Indexation"

        override fun isDisabled(): Boolean = false

    }

    private fun createSCMBuildCommitIndexCleanupJob() = object : Job {
        override fun getKey(): JobKey =
            SCMJobs.category
                .getType("build-commit-index").withName("SCM Build Commit Indexes")
                .getKey("cleanup")

        override fun getTask() = JobRun { listener ->
            listener.message("Clearing the SCM build/commit links for all projects...")
            scmBuildCommitIndexService.clearBuildCommits()
        }

        override fun getDescription(): String = "SCM Build Commit Index Cleanup"

        override fun isDisabled(): Boolean = false

    }

}