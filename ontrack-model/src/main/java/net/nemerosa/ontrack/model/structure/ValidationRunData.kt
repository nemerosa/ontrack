package net.nemerosa.ontrack.model.structure

/**
 * Data associated with a [ValidationRun] and
 * a [ValidationDataType] by ID.
 *
 * @param T Type of data
 * @param descriptor Type descriptor
 * @param data Data
 */
class ValidationRunData<out T>(
        val descriptor: ValidationDataTypeDescriptor,
        val data: T
)

fun <C, T> ValidationDataType<C, T>.data(data: T) =
        ValidationRunData(
                descriptor,
                data
        )

