package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.security.BuildCreate
import net.nemerosa.ontrack.model.security.ProjectFunction
import net.nemerosa.ontrack.model.security.ValidationRunCreate
import kotlin.reflect.KClass

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
enum class RunnableEntityType(
        val projectFunction: KClass<out ProjectFunction>,
        private val loader: StructureService.(Int) -> RunnableEntity
) {
    build(
            BuildCreate::class,
            { getBuild(ID.of(it)) }
    ),
    validation_run(
            ValidationRunCreate::class,
            { getValidationRun(ID.of(it)) }
    );

    fun load(structureService: StructureService, id: Int) =
            structureService.loader(id)
}
