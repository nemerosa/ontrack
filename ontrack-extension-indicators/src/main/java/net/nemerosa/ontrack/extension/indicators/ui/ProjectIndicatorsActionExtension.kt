package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.api.ProjectEntityActionExtension
import net.nemerosa.ontrack.extension.indicators.IndicatorsExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component
import java.util.*

@Component
class ProjectIndicatorsActionExtension(
        extension: IndicatorsExtensionFeature
) : AbstractExtension(extension), ProjectEntityActionExtension {

    override fun getAction(entity: ProjectEntity): Optional<Action> {
        return if (entity is Project) {
            Optional.of(
                    Action.of(
                            "project-indicators",
                            "Project indicators",
                            "project-indicators/${entity.id()}"
                    )
            )
        } else {
            Optional.empty()
        }
    }

}