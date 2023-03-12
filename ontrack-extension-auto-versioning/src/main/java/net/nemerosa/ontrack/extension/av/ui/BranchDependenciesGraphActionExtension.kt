package net.nemerosa.ontrack.extension.av.ui

import net.nemerosa.ontrack.extension.api.ProjectEntityActionExtension
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component
import java.util.*

@Component
class BranchDependenciesGraphActionExtension(
    extensionFeature: AutoVersioningExtensionFeature,
) : AbstractExtension(extensionFeature), ProjectEntityActionExtension {

    override fun getAction(entity: ProjectEntity): Optional<Action> {
        return if (entity is Branch) {
            Optional.of(
                Action.of(
                    "auto-versioning-dependency-graph",
                    "Dependency graph",
                    "dependency-graph/branch/${entity.id}/downstream"
                )
            )
        } else {
            Optional.empty()
        }
    }
}