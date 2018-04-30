package net.nemerosa.ontrack.model.structure

/**
 * [ProjectEntity] which can be associated with some [RunInfo].
 */
interface RunnableEntity : ProjectEntity {
    /**
     * Gets the type of runnable entity
     */
    val runnableEntityType: RunnableEntityType
}

/**
 * Known list of [RunnableEntity] (not extensible).
 */
enum class RunnableEntityType(private val loader: StructureService.(Int) -> RunnableEntity) {
    build({ getBuild(ID.of(it)) }),
    validation_run({ getValidationRun(ID.of(it)) });

    fun load(structureService: StructureService, id: Int) =
            structureService.loader(id)
}
