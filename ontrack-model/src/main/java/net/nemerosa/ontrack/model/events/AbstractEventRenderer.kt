package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.*

abstract class AbstractEventRenderer : EventRenderer {

    protected fun getProjectEntityName(projectEntity: ProjectEntity): String =
        when (projectEntity.projectEntityType) {
            ProjectEntityType.PROJECT -> (projectEntity as Project).name
            ProjectEntityType.BRANCH -> (projectEntity as Branch).name
            ProjectEntityType.PROMOTION_LEVEL -> (projectEntity as PromotionLevel).name
            ProjectEntityType.VALIDATION_STAMP -> (projectEntity as ValidationStamp).name
            ProjectEntityType.BUILD -> (projectEntity as Build).name
            ProjectEntityType.VALIDATION_RUN -> "#" + (projectEntity as ValidationRun).runOrder
            ProjectEntityType.PROMOTION_RUN -> {
                val (_, build, promotionLevel) = projectEntity as PromotionRun
                "${build.name}->${promotionLevel.name}"
            }
        }
}