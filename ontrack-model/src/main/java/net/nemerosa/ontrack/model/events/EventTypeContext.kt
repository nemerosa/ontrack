package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.ProjectEntityType

/**
 * Context for an event.
 *
 * Used for description purposes only.
 */
data class EventTypeContext(
    val items: Map<String, EventTypeContextItem>,
)

fun emptyEventContext() = EventTypeContext(
    items = emptyMap(),
)

sealed interface EventTypeContextItem {
    val description: String
}

data class EventTypeContextEntity(
    val projectEntityType: ProjectEntityType,
    override val description: String,
) : EventTypeContextItem

data class EventTypeContextAnyEntity(
    override val description: String,
) : EventTypeContextItem

data class EventTypeContextValue(
    override val description: String,
) : EventTypeContextItem

fun eventContext(
    vararg items: Pair<String, EventTypeContextItem>
) = EventTypeContext(
    items = items.toMap()
)

private fun eventEntity(
    projectEntityType: ProjectEntityType,
    description: String,
) = EventTypeContextEntity(
    projectEntityType,
    description
)

fun eventAnyEntity(description: String) = "entity" to EventTypeContextAnyEntity(description)

fun eventProject(description: String) = "project" to eventEntity(ProjectEntityType.PROJECT, description)
fun eventBranch(description: String) = "branch" to eventEntity(ProjectEntityType.BRANCH, description)
fun eventBuild(description: String) = "build" to eventEntity(ProjectEntityType.BUILD, description)
fun eventPromotionLevel(description: String) =
    "promotionLevel" to eventEntity(ProjectEntityType.PROMOTION_LEVEL, description)

fun eventPromotionRun(description: String) = "promotionRun" to eventEntity(ProjectEntityType.PROMOTION_RUN, description)
fun eventXPromotionRun(description: String) = "xPromotionRun" to eventEntity(ProjectEntityType.PROMOTION_RUN, description)
fun eventValidationStamp(description: String) =
    "validationStamp" to eventEntity(ProjectEntityType.VALIDATION_STAMP, description)

fun eventValidationRun(description: String) =
    "validationRun" to eventEntity(ProjectEntityType.VALIDATION_RUN, description)

fun eventValue(
    name: String,
    description: String,
) = name to EventTypeContextValue(
    description
)
