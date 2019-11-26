package net.nemerosa.ontrack.extension.issues

import net.nemerosa.ontrack.extension.api.ProjectEntityActionExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component
import java.util.*

@Component
class BranchIssuesActionExtension(
        extensionFeature: IssuesExtensionFeature,
        private val issueServiceExtensionService: IssueServiceExtensionService
) : AbstractExtension(extensionFeature), ProjectEntityActionExtension {

    override fun getAction(entity: ProjectEntity): Optional<Action> = if (entity is Branch) {
        val issueService = issueServiceExtensionService.getIssueServiceExtension(entity.project)
        if (issueService != null) {
            Optional.of(Action.of(
                    "branch-issues",
                    "Branch issues",
                    "branch-issues/${entity.id()}"
            ))
        } else {
            Optional.empty()
        }
    } else {
        Optional.empty()
    }

}