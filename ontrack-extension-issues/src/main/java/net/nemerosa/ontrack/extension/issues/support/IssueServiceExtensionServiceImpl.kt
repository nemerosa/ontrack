package net.nemerosa.ontrack.extension.issues.support

import net.nemerosa.ontrack.extension.issues.IssueServiceExtensionService
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Service

@Service
class IssueServiceExtensionServiceImpl(
        private val contributors: List<IssueServiceExtensionContributor>
) : IssueServiceExtensionService {

    override fun getIssueServiceExtension(project: Project): ConfiguredIssueService? =
            contributors.mapNotNull {
                it.getIssueServiceExtension(project)
            }.firstOrNull()

}