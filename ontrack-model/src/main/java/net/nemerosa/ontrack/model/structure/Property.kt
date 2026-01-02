package net.nemerosa.ontrack.model.structure

/**
 * Property value, associated with its type.
 * 
 * @param type     Type for this property
 * @param value    Value for this property
 * @param editable Editable status
 */
data class Property<T>(
    val type: PropertyType<T>,
    val value: T?,
    val editable: Boolean = false,
) {

    /**
     * Descriptor for the property type
     */
    val typeDescriptor: PropertyTypeDescriptor = PropertyTypeDescriptor.of(type)

    /**
     * Editable property
     */
    fun editable(editable: Boolean): Property<T> = copy(editable = editable)

    companion object {

        fun <T> empty(type: PropertyType<T>): Property<T> {
            return Property(type, null, false)
        }

        fun <T> of(type: PropertyType<T>, value: T?): Property<T> {
            return Property(type, value, false)
        }
    }
}
