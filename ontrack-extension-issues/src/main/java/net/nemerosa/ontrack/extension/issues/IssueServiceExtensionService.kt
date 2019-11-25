package net.nemerosa.ontrack.extension.issues

import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.model.structure.Project

/**
* High level access to [IssueServiceExtension] from components of the Ontrack model.
*/
interface IssueServiceExtensionService {

    /**
     * Given a project, returns any associated [ConfiguredIssueService].
     */
    fun getIssueServiceExtension(project: Project): ConfiguredIssueService?

}