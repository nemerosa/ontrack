package net.nemerosa.ontrack.extension.av.ui

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.api.ProjectEntityActionExtension
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component
import java.util.*

@Component
class BranchDependenciesGraphActionExtension(
    extensionFeature: AutoVersioningExtensionFeature,
    private val structureService: StructureService,
) : AbstractExtension(extensionFeature), ProjectEntityActionExtension {

    override fun getAction(entity: ProjectEntity): Optional<Action> {
        return if (entity is Branch) {
            // Gets the last build
            val lastBuild = structureService.getLastBuild(entity.id).getOrNull()
            if (lastBuild != null) {
                Optional.of(
                    Action.of(
                        "auto-versioning-dependency-graph",
                        "Dependency graph",
                        "dependency-graph/build/${lastBuild.id}"
                    )
                )
            } else {
                Optional.empty()
            }
        } else {
            Optional.empty()
        }
    }
}