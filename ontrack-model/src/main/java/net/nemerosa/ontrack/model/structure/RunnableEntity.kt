package net.nemerosa.ontrack.model.structure

/**
 * [ProjectEntity] which can be associated with some [RunInfo].
 */
interface RunnableEntity : ProjectEntity

/**
 * Known list of [RunnableEntity] (not extensible).
 */
enum class RunnableEntityType {
    build,
    validation_run
}
