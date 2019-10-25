package net.nemerosa.ontrack.model.form

/**
 * Definition of a [Form] field.
 */
interface Field {

    /**
     * ID identifying the type of field. Used for mapping as client side.
     */
    val type: String

    /**
     * Name of this field in the form.
     */
    val name: String

    /**
     * Label associated with this field
     */
    val label: String

    /**
     * Help text for this field (optional).
     */
    val help: String

    /**
     * Value associated with this field. Can be `null`.
     */
    val value: Any?

    /**
     * Associates a value with this field and returns a field instance.
     */
    fun value(value: Any?): Field
}
