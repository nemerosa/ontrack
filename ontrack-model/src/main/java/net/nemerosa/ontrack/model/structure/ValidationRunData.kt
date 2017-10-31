package net.nemerosa.ontrack.model.structure

import kotlin.reflect.KClass

/**
 * Data associated with a [ValidationRun] and
 * a [ValidationDataType] by ID.
 *
 * @param T Type of data
 * @param id Type ID
 * @param data Data
 */
class ValidationRunData<out T>(
        val id: String,
        val data: T
)

fun <C, T> KClass<out ValidationDataType<C, T>>.validationRunData(data: T) =
        ValidationRunData(
                this.qualifiedName!!,
                data
        )
