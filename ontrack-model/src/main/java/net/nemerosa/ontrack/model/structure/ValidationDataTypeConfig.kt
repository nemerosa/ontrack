package net.nemerosa.ontrack.model.structure

import kotlin.reflect.KClass

/**
 * Configuration data associated with a [ValidationStamp] and
 * a [ValidationDataType] by ID.
 *
 * @param C Type of configuration data
 * @param id Type ID
 * @param config Configuration data
 */
class ValidationDataTypeConfig<out C>(
        val id: String,
        val config: C?
)

fun <C, T> KClass<out ValidationDataType<C, T>>.validationDataTypeConfig(config: C?) =
        ValidationDataTypeConfig(
                this.qualifiedName!!,
                config
        )
