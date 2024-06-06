package net.nemerosa.ontrack.model.templating

import kotlin.reflect.KClass

/**
 * Representation of a field in a [TemplatingRenderable].
 *
 * @property name Name of the field
 * @property description Description of the field
 * @property config Class of the field (for the documentation)
 */
data class TemplatingRenderableDocField(
    val name: String,
    val description: String,
    val config: KClass<*>,
)
