package net.nemerosa.ontrack.model.templating

import net.nemerosa.ontrack.model.events.EventRenderer

/**
 * [TemplatingRenderable] which can be used to make a map of
 * "environment" variables" available.
 *
 * For example:
 *
 * ```
 * templatingService.render(
 *     template = template,
 *     context = mapOf(
 *         "env" to EnvTemplatingRenderable(env),
 *     ),
 *     renderer = PlainEventRenderer.INSTANCE,
 * )
 * ```
 *
 * where `env` is a map of environment variables.
 */
class EnvTemplatingRenderable(
    private val env: Map<String, String>,
) : TemplatingRenderable {
    override fun render(
        field: String?,
        configMap: Map<String, String>,
        renderer: EventRenderer
    ): String =
        if (field.isNullOrBlank()) {
            throw TemplatingRenderableFieldRequiredException()
        } else {
            val value = env[field]
                ?: throw TemplatingRenderableFieldNotFoundException(field)
            value
        }
}