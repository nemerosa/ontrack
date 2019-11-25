package net.nemerosa.ontrack.extension.issues.support

import net.nemerosa.ontrack.extension.issues.IssueServiceExtension
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

/**
 * High level access to [IssueServiceExtension] from components of the Ontrack model.
 */
interface IssueServiceExtensionContributor {

    /**
     * Given a project, returns any associated [ConfiguredIssueService].
     */
    fun getIssueServiceExtension(project: Project): ConfiguredIssueService?

}

@Component
class NOPIssueServiceExtensionContributor : IssueServiceExtensionContributor {
    override fun getIssueServiceExtension(project: Project): ConfiguredIssueService? = null
}