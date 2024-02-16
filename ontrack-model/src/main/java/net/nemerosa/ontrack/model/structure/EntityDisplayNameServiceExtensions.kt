package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.events.EventRenderer

val ProjectEntity.defaultDisplayName: String
    get() = when (this) {
        is Project -> name
        is Branch -> name
        is Build -> name
        is ValidationStamp -> name
        is PromotionLevel -> name
        is ValidationRun -> "#$runOrder"
        is PromotionRun -> "${build.name}/${promotionLevel.name}"
        else -> throw IllegalStateException("Unknown project entity: $this")
    }

fun EntityDisplayNameService.render(entity: ProjectEntity, renderer: EventRenderer) =
    renderer.render(entity, getEntityDisplayName(entity))
