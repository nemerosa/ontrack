package net.nemerosa.ontrack.model.structure

/**
 * Association between a validation stamp name and some validation run data.
 *
 * @property name Name of the validation stamp
 * @property type Type of data associated with the validation run
 * @property data Data associated with the validation run
 */
class ValidationRunDataRequest(
        val name: String,
        val type: String? = null,
        val data: Any? = null
)
