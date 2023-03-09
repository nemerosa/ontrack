package net.nemerosa.ontrack.extension.av.ui

import net.nemerosa.ontrack.extension.api.ProjectEntityActionExtension
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component
import java.util.*

@Component
class BuildDependenciesGraphActionExtension(
    extensionFeature: AutoVersioningExtensionFeature,
) : AbstractExtension(extensionFeature), ProjectEntityActionExtension {

    override fun getAction(entity: ProjectEntity): Optional<Action> {
        return if (entity is Build) {
            Optional.of(
                Action.of(
                    "auto-versioning-dependency-graph",
                    "Dependency graph",
                    "dependency-graph/build/${entity.id}"
                )
            )
        } else {
            Optional.empty()
        }
    }
}