package net.nemerosa.ontrack.extension.github.ingestion.settings

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.casc.context.settings.AbstractSubSettingsContext
import net.nemerosa.ontrack.extension.casc.schema.CascType
import net.nemerosa.ontrack.extension.casc.schema.cascField
import net.nemerosa.ontrack.extension.casc.schema.cascObject
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class GitHubIngestionSettingsCasc(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
) : AbstractSubSettingsContext<GitHubIngestionSettings>(
    "github-ingestion",
    GitHubIngestionSettings::class,
    settingsManagerService,
    cachedSettingsService
) {

    override val type: CascType = cascObject(
        "GitHub ingestion settings",
        cascField(GitHubIngestionSettings::token, required = true),
        cascField(GitHubIngestionSettings::retentionDays, required = false),
        cascField(GitHubIngestionSettings::orgProjectPrefix, required = false),
        cascField(GitHubIngestionSettings::indexationInterval, required = false),
        cascField(GitHubIngestionSettings::repositoryIncludes, required = false),
        cascField(GitHubIngestionSettings::repositoryExcludes, required = false),
        cascField(GitHubIngestionSettings::issueServiceIdentifier, required = false),
        cascField(GitHubIngestionSettings::enabled, required = false),
    )

    override fun adjustNodeBeforeParsing(node: JsonNode): JsonNode =
        node.ifMissing(
            GitHubIngestionSettings::retentionDays to GitHubIngestionSettings.DEFAULT_RETENTION_DAYS,
            GitHubIngestionSettings::orgProjectPrefix to GitHubIngestionSettings.DEFAULT_ORG_PROJECT_PREFIX,
            GitHubIngestionSettings::indexationInterval to GitHubIngestionSettings.DEFAULT_INDEXATION_INTERVAL,
            GitHubIngestionSettings::repositoryIncludes to GitHubIngestionSettings.DEFAULT_REPOSITORY_INCLUDES,
            GitHubIngestionSettings::repositoryExcludes to GitHubIngestionSettings.DEFAULT_REPOSITORY_EXCLUDES,
            GitHubIngestionSettings::issueServiceIdentifier to GitHubIngestionSettings.DEFAULT_ISSUE_SERVICE_IDENTIFIER,
            GitHubIngestionSettings::enabled to GitHubIngestionSettings.DEFAULT_ENABLED,
        )
}