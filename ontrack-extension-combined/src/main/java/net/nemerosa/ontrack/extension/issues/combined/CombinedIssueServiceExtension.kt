package net.nemerosa.ontrack.extension.issues.combined

import net.nemerosa.ontrack.common.asOptional
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.issues.export.ExportFormat
import net.nemerosa.ontrack.extension.issues.export.ExportedIssues
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.support.MessageAnnotator
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import java.util.*

@Component
class CombinedIssueServiceExtension(
        extensionFeature: CombinedIssueServiceExtensionFeature,
        private val issueServiceRegistry: IssueServiceRegistry,
        private val configurationService: CombinedIssueServiceConfigurationService
) : AbstractExtension(extensionFeature), IssueServiceExtension {

    /**
     * Gets the list of attached configured issue services.
     *
     * @param issueServiceConfiguration Configuration of the combined issue service
     * @return List of associated configured issue services
     */
    protected fun getConfiguredIssueServices(issueServiceConfiguration: IssueServiceConfiguration): Collection<ConfiguredIssueService> {
        val combinedIssueServiceConfiguration = issueServiceConfiguration as CombinedIssueServiceConfiguration
        return combinedIssueServiceConfiguration
                .issueServiceConfigurationIdentifiers
                .map { issueServiceRegistry.getConfiguredIssueService(it) }
    }

    override fun getId(): String {
        return SERVICE
    }

    override fun getName(): String {
        return "Combined issue service"
    }

    override fun getConfigurationList(): List<IssueServiceConfiguration> {
        return configurationService.configurationList
    }

    override fun getConfigurationByName(name: String): IssueServiceConfiguration {
        return configurationService.getConfigurationByName(name).orElse(null)
    }

    /**
     * Without any specific configuration, we have to assume the token is valid.
     */
    override fun validIssueToken(token: String): Boolean {
        return true
    }

    override fun getMessageRegex(issueServiceConfiguration: IssueServiceConfiguration, issue: Issue): String {
        return getConfiguredIssueServices(issueServiceConfiguration)
                .stream()
                .findFirst()
                .map { o -> o.getMessageRegex(issue) }
                .orElse("")
    }

    override fun extractIssueKeysFromMessage(issueServiceConfiguration: IssueServiceConfiguration, message: String): Set<String> {
        return getConfiguredIssueServices(issueServiceConfiguration)
                .map { configuredIssueService ->
                    configuredIssueService.issueServiceExtension.extractIssueKeysFromMessage(
                            configuredIssueService.issueServiceConfiguration,
                            message
                    )
                }
                .fold(emptySet()) { acc, values ->
                    acc + values
                }
    }

    override fun getMessageAnnotator(issueServiceConfiguration: IssueServiceConfiguration): Optional<MessageAnnotator> {
        // Gets all the defined message annotators
        val messageAnnotators = getConfiguredIssueServices(issueServiceConfiguration)
                .mapNotNull { configuredIssueService ->
                    configuredIssueService.issueServiceExtension.getMessageAnnotator(
                            configuredIssueService.issueServiceConfiguration
                    ).getOrNull()
                }
        return if (messageAnnotators.isEmpty()) {
            Optional.empty()
        } else {
            // For each message annotator, gets the list of annotation
            val messageAnnotator = MessageAnnotator { text ->
                messageAnnotators
                        .map { messageAnnotator -> messageAnnotator.annotate(text).toSet() }
                        .fold(emptySet()) { acc, values -> acc + values }
            }
            Optional.of(messageAnnotator)
        }
    }

    override fun getLinkForAllIssues(issueServiceConfiguration: IssueServiceConfiguration, issues: List<Issue>): String? {
        return null
    }

    override fun getIssue(issueServiceConfiguration: IssueServiceConfiguration, issueKey: String): Issue? {
        return getConfiguredIssueServices(issueServiceConfiguration)
                .mapNotNull { configuredIssueService ->
                    configuredIssueService.issueServiceExtension.getIssue(
                            configuredIssueService.issueServiceConfiguration,
                            issueKey
                    )
                }
                .firstOrNull()
    }

    override fun exportFormats(issueServiceConfiguration: IssueServiceConfiguration): List<ExportFormat> {
        return getConfiguredIssueServices(issueServiceConfiguration)
                .map { configuredIssueService ->
                    configuredIssueService.issueServiceExtension.exportFormats(
                            configuredIssueService.issueServiceConfiguration
                    ).toSet()
                }
                .fold(emptySet<ExportFormat>()) { acc, v -> acc + v }
                .toList()
    }

    override fun exportIssues(issueServiceConfiguration: IssueServiceConfiguration, issues: List<Issue>, request: IssueChangeLogExportRequest): ExportedIssues {
        val exportedIssues = getConfiguredIssueServices(issueServiceConfiguration)
                .map { configuredIssueService ->
                    configuredIssueService.issueServiceExtension.exportIssues(
                            configuredIssueService.issueServiceConfiguration,
                            issues,
                            request
                    )
                }
        // Checks the format is the same for all exports (it must)
        check(exportedIssues.all { it -> StringUtils.equals(it.format, request.format) }) { "All exported issues must have the same export format" }
        // Concatenates the content
        return ExportedIssues(
                request.format,
                exportedIssues.joinToString("") { it.content }
        )
    }

    override fun getIssueId(issueServiceConfiguration: IssueServiceConfiguration, token: String): Optional<String> {
        return getConfiguredIssueServices(issueServiceConfiguration)
                .mapNotNull { configuredIssueService ->
                    configuredIssueService.issueServiceExtension.getIssueId(
                            configuredIssueService.issueServiceConfiguration,
                            token
                    ).getOrNull()
                }
                .firstOrNull().asOptional()
    }

    companion object {
        const val SERVICE = "combined"
    }
}
