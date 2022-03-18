package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.GitConfigProperties
import net.nemerosa.ontrack.git.GitRepositoryClientFactory
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.support.JobProvider
import org.springframework.stereotype.Component

/**
 * Job to cleanup existing indexations.
 */
@Component
class GitIndexationCleanupJob(
    private val gitConfigProperties: GitConfigProperties,
    private val gitRepositoryClientFactory: GitRepositoryClientFactory,
) : JobProvider {

    override fun getStartingJobs() = if (gitConfigProperties.indexation.cleanup.enabled) {
        listOf(
            createGitIndexationCleanupJobRegistration()
        )
    } else {
        emptyList()
    }

    private fun createGitIndexationCleanupJobRegistration() = JobRegistration(
        createGitIndexationCleanupJob(),
        if (gitConfigProperties.indexation.cleanup.cron.isNotBlank()) {
            Schedule.cron(gitConfigProperties.indexation.cleanup.cron)
        } else {
            Schedule.NONE
        }
    )

    private fun createGitIndexationCleanupJob() = object : Job {

        override fun getKey(): JobKey =
            GIT_JOB_CATEGORY.getType("indexation-cleanup").withName("Git Indexation Cleanup")
                .getKey("main")

        override fun getTask() = JobRun {
            gitRepositoryClientFactory.reset()
        }

        override fun getDescription(): String = "Resetting all Git indexations."

        override fun isDisabled(): Boolean = false

    }

}