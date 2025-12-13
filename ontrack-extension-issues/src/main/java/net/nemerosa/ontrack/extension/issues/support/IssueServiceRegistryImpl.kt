package net.nemerosa.ontrack.extension.issues.support

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationIdentifier.Companion.parse
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation.Companion.of
import org.springframework.stereotype.Service

@Service
class IssueServiceRegistryImpl(
    private val extensionManager: ExtensionManager
) : IssueServiceRegistry {

    private val issueServiceExtensionMap: Map<String, IssueServiceExtension> by lazy {
        extensionManager.getExtensions(IssueServiceExtension::class.java).associateBy { it.id }
    }

    override val issueServices: Collection<IssueServiceExtension>
        get() {
            return issueServiceExtensionMap.values
        }

    override fun findIssueServiceById(id: String): IssueServiceExtension? = issueServiceExtensionMap[id]

    override val availableIssueServiceConfigurations: List<IssueServiceConfigurationRepresentation>
        get() {
            val issueServiceConfigurationRepresentations = mutableListOf<IssueServiceConfigurationRepresentation>()
            for (issueServiceExtension in issueServiceExtensionMap.values) {
                val configurationList = issueServiceExtension.getConfigurationList()
                for (issueServiceConfiguration in configurationList) {
                    issueServiceConfigurationRepresentations.add(
                        of(
                            issueServiceExtension,
                            issueServiceConfiguration
                        )
                    )
                }
            }
            return issueServiceConfigurationRepresentations
        }

    override fun getConfiguredIssueService(issueServiceConfigurationIdentifier: String): ConfiguredIssueService? {
        // Parsing
        val identifier = parse(issueServiceConfigurationIdentifier)
        if (identifier != null) {
            val issueService = findIssueServiceById(identifier.serviceId)
            return issueService?.run {
                val config = getConfigurationByName(identifier.name)
                config?.let {
                    ConfiguredIssueService(
                        this,
                        it
                    )
                }
            }
        } else {
            return null
        }
    }
}
