package net.nemerosa.ontrack.model.structure

/**
 * Configuration data associated with a [ValidationStamp] and
 * a [ValidationDataType] by ID.
 *
 * @param C Type of configuration data
 * @param descriptor Type descriptor
 * @param config Configuration data
 */
class ValidationDataTypeConfig<out C>(
        val descriptor: ValidationDataTypeDescriptor,
        val config: C?
)

fun <C, T> ValidationDataType<C, T>.config(config: C?) =
        ValidationDataTypeConfig(
                descriptor,
                config
        )
