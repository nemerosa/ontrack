package net.nemerosa.ontrack.extension.github.ingestion.settings

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName

/**
 * Settings for the ingestion of GitHub workflows.
 *
 * @property token Secret token sent by the GitHub hook and signing the payload
 * @property retentionDays Number of days to keep the received payloads (0 = forever)
 * @property orgProjectPrefix Must the organization name be used as a project name prefix?
 * @property indexationInterval Default indexation interval when configuring the GitHub projects
 * @property repositoryIncludes Regular expression to include repositories
 * @property repositoryExcludes Regular expression to exclude repositories
 * @property jobIncludes Regular expression to include jobs
 * @property jobExcludes Regular expression to exclude jobs
 * @property stepIncludes Regular expression to include steps
 * @property stepExcludes Regular expression to exclude steps
 */
class GitHubIngestionSettings(
    @APIDescription("Secret token sent by the GitHub hook and signing the payload")
    val token: String,
    @APIDescription("Number of days to keep the received payloads (0 = forever)")
    val retentionDays: Int = DEFAULT_RETENTION_DAYS,
    @APIDescription("Must the organization name be used as a project name prefix?")
    val orgProjectPrefix: Boolean = DEFAULT_ORG_PROJECT_PREFIX,
    @APIDescription("Default indexation interval when configuring the GitHub projects")
    val indexationInterval: Int = DEFAULT_INDEXATION_INTERVAL,
    @APIName("Include repositories")
    @APIDescription("Regular expression to include repositories")
    val repositoryIncludes: String = DEFAULT_REPOSITORY_INCLUDES,
    @APIName("Exclude repositories")
    @APIDescription("Regular expression to exclude repositories")
    val repositoryExcludes: String = DEFAULT_REPOSITORY_EXCLUDES,
    @APIName("Include jobs")
    @APIDescription("Regular expression to include jobs")
    val jobIncludes: String = DEFAULT_JOB_INCLUDES,
    @APIName("Exclude jobs")
    @APIDescription("Regular expression to exclude jobs")
    val jobExcludes: String = DEFAULT_JOB_EXCLUDES,
    @APIName("Include steps")
    @APIDescription("Regular expression to include steps")
    val stepIncludes: String = DEFAULT_STEP_INCLUDES,
    @APIName("Exclude steps")
    @APIDescription("Regular expression to exclude steps")
    val stepExcludes: String = DEFAULT_STEP_EXCLUDES,
) {
    companion object {
        /**
         * Keeping the payloads 30 days by default.
         */
        const val DEFAULT_RETENTION_DAYS = 30

        /**
         * Not using the organization as a project's name prefix
         */
        const val DEFAULT_ORG_PROJECT_PREFIX = false

        /**
         * 30 minutes by default for the indexation interval
         */
        const val DEFAULT_INDEXATION_INTERVAL = 30

        /**
         * By default, including all repositories
         */
        const val DEFAULT_REPOSITORY_INCLUDES = ".*"

        /**
         * By default, not excluding any repository
         */
        const val DEFAULT_REPOSITORY_EXCLUDES = ""

        /**
         * By default, including all jobs
         */
        const val DEFAULT_JOB_INCLUDES = ".*"

        /**
         * By default, not excluding any job
         */
        const val DEFAULT_JOB_EXCLUDES = ""

        /**
         * By default, including all steps
         */
        const val DEFAULT_STEP_INCLUDES = ".*"

        /**
         * By default, not excluding any step
         */
        const val DEFAULT_STEP_EXCLUDES = ""
    }
}
