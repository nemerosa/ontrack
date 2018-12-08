package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueServiceProvider
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class BasicGitConfigurationIssueServiceProvider(
        private val issueServiceRegistry: IssueServiceRegistry,
        private val propertyService: PropertyService
) : ConfiguredIssueServiceProvider {
    override fun getConfiguredIssueService(project: Project): ConfiguredIssueService? {
        return propertyService.getProperty(
                project,
                GitProjectConfigurationPropertyType::class.java
        )
                .value
                ?.configuration
                ?.issueServiceConfigurationIdentifier
                ?.let {
                    issueServiceRegistry.getConfiguredIssueService(it)
                }
    }
}