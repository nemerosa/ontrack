package net.nemerosa.ontrack.extension.github.ingestion.settings

import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel

/**
 * Settings for the ingestion of GitHub workflows.
 *
 * @property token Secret token sent by the GitHub hook and signing the payload
 * @property retentionDays Number of days to keep the received payloads (0 = forever)
 * @property orgProjectPrefix Must the organization name be used as a project name prefix?
 * @property indexationInterval Default indexation interval when configuring the GitHub projects
 * @property repositoryIncludes Regular expression to include repositories
 * @property repositoryExcludes Regular expression to exclude repositories
 * @property enabled Is the ingestion of the GitHub events enabled?
 */
class GitHubIngestionSettings(
    @APIDescription("Secret token sent by the GitHub hook and signing the payload. This MUST be a valid Ontrack API token.")
    val token: String? = null,
    @APIDescription("Number of days to keep the received payloads (0 = forever)")
    val retentionDays: Int = DEFAULT_RETENTION_DAYS,
    @APIDescription("Must the organization name be used as a project name prefix?")
    val orgProjectPrefix: Boolean = DEFAULT_ORG_PROJECT_PREFIX,
    @APIDescription("Default indexation interval when configuring the GitHub projects")
    val indexationInterval: Int = DEFAULT_INDEXATION_INTERVAL,
    @APILabel("Include repositories")
    @APIDescription("Regular expression to include repositories")
    val repositoryIncludes: String = DEFAULT_REPOSITORY_INCLUDES,
    @APILabel("Exclude repositories")
    @APIDescription("Regular expression to exclude repositories")
    val repositoryExcludes: String = DEFAULT_REPOSITORY_EXCLUDES,
    @APILabel("Default issue service identifier")
    @APIDescription("Identifier of the issue service to use by default. For example `self` for GitHub issues or `jira//config`.")
    val issueServiceIdentifier: String = DEFAULT_ISSUE_SERVICE_IDENTIFIER,
    @APILabel("Ingestion enabled")
    @APIDescription("Is the ingestion of the GitHub events enabled?")
    val enabled: Boolean = DEFAULT_ENABLED,
) {

    fun obfuscate() =GitHubIngestionSettings(
        token = "",
        retentionDays = retentionDays,
        orgProjectPrefix = orgProjectPrefix,
        indexationInterval = indexationInterval,
        repositoryIncludes = repositoryIncludes,
        repositoryExcludes = repositoryExcludes,
        issueServiceIdentifier = issueServiceIdentifier,
        enabled = enabled
    )

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
         * By default, using the GitHub issues.
         */
        const val DEFAULT_ISSUE_SERVICE_IDENTIFIER = IssueServiceConfigurationRepresentation.SELF_ID

        /**
         * By default, enabling the ingestion
         */
        const val DEFAULT_ENABLED = true
    }
}
