package net.nemerosa.ontrack.model.structure

/**
 * Definition of a template parameter.
 *
 * @property name Name of the parameter, used for template expressions.
 * @property description Description of the parameter.
 * @property expression Expression used for synchronisation
 */
data class TemplateParameter(
        val name: String,
        val description: String,
        val expression: String
)
