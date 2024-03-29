package net.nemerosa.ontrack.extension.issues.combined

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
import kotlin.jvm.optionals.getOrNull

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
            .mapNotNull { issueServiceRegistry.getConfiguredIssueService(it) }
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

    override fun getMessageRegex(issueServiceConfiguration: IssueServiceConfiguration, issue: Issue): String {
        return getConfiguredIssueServices(issueServiceConfiguration)
            .firstOrNull()
            ?.getMessageRegex(issue)
            ?: ""
    }

    override fun extractIssueKeysFromMessage(
        issueServiceConfiguration: IssueServiceConfiguration,
        message: String
    ): Set<String> {
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

    override fun getMessageAnnotator(issueServiceConfiguration: IssueServiceConfiguration): MessageAnnotator? {
        // Gets all the defined message annotators
        val messageAnnotators = getConfiguredIssueServices(issueServiceConfiguration)
            .mapNotNull { configuredIssueService ->
                configuredIssueService.issueServiceExtension.getMessageAnnotator(
                    configuredIssueService.issueServiceConfiguration
                )
            }
        return if (messageAnnotators.isEmpty()) {
            null
        } else {
            // For each message annotator, gets the list of annotation
            val messageAnnotator = MessageAnnotator { text ->
                messageAnnotators
                    .map { messageAnnotator -> messageAnnotator.annotate(text).toSet() }
                    .fold(emptySet()) { acc, values -> acc + values }
            }
            messageAnnotator
        }
    }

    override fun getIssue(issueServiceConfiguration: IssueServiceConfiguration, issueKey: String): Issue? {
        return getConfiguredIssueServices(issueServiceConfiguration).firstNotNullOfOrNull { configuredIssueService ->
            configuredIssueService.issueServiceExtension.getIssue(
                configuredIssueService.issueServiceConfiguration,
                issueKey
            )
        }
    }

    @Deprecated("Deprecated in Java")
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

    @Deprecated("Deprecated in Java")
    override fun exportIssues(
        issueServiceConfiguration: IssueServiceConfiguration,
        issues: List<Issue>,
        request: IssueChangeLogExportRequest
    ): ExportedIssues {
        val exportedIssues = getConfiguredIssueServices(issueServiceConfiguration)
            .map { configuredIssueService ->
                configuredIssueService.issueServiceExtension.exportIssues(
                    configuredIssueService.issueServiceConfiguration,
                    issues,
                    request
                )
            }
        // Checks the format is the same for all exports (it must)
        check(exportedIssues.all { it ->
            StringUtils.equals(
                it.format,
                request.format
            )
        }) { "All exported issues must have the same export format" }
        // Concatenates the content
        return ExportedIssues(
            request.format,
            exportedIssues.mapNotNull { exportedIssue ->
                exportedIssue.content.takeIf { it.isNotBlank() }
            }.joinToString("\n")
        )
    }

    override fun getIssueId(issueServiceConfiguration: IssueServiceConfiguration, token: String): String? {
        return getConfiguredIssueServices(issueServiceConfiguration)
            .firstNotNullOfOrNull { configuredIssueService ->
                configuredIssueService.issueServiceExtension.getIssueId(
                    configuredIssueService.issueServiceConfiguration,
                    token
                )
            }
    }

    override fun getIssueTypes(
        issueServiceConfiguration: IssueServiceConfiguration,
        issue: Issue,
    ): Set<String> =
        getConfiguredIssueServices(issueServiceConfiguration).map {
            it.issueServiceExtension.getIssueTypes(it.issueServiceConfiguration, issue)
        }.reduce { acc, set ->
            acc + set
        }

    companion object {
        const val SERVICE = "combined"
    }
}
