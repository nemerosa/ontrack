package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.common.SimpleExpand
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.templating.*
import org.springframework.stereotype.Service

@Service
class TemplatingServiceImpl(
    templatingSources: List<TemplatingSource>,
    templatingFilters: List<TemplatingFilter>,
) : TemplatingService {

    private val sourcesPerProjectEntityType = ProjectEntityType.values().associate { type ->
        type to templatingSources.filter { source ->
            source.validFor(type)
        }
    }

    private val filtersById = templatingFilters.associateBy { it.id }

    private val regexExpressions =
        "\\$\\{([^\\}]+)\\}".toRegex()

    @Suppress("RegExpUnnecessaryNonCapturingGroup")
    private val regexToken =
        "^([a-z][a-zA-Z]+)(?:\\.([a-zA-Z_-]+))?(?:\\?((?:[a-zA-Z]+=[a-zA-Z0-9\\s_-]+)(?:&[a-zA-Z]+=[a-zA-Z0-9\\s_-]+)*))?(?:\\|([a-zA-Z_-]+))?\$".toRegex()

    @Deprecated("Legacy templates will be removed in V5.")
    override fun isLegacyTemplate(template: String): Boolean =
        SimpleExpand.regex.containsMatchIn(template) &&
                !regexExpressions.containsMatchIn(template)

    override fun render(
        template: String,
        context: Map<String, Any>,
        renderer: EventRenderer,
    ): String =
        regexExpressions.replace(template) { m ->
            val expression = m.groupValues[1]
            renderExpression(
                expression = expression,
                context = context,
                renderer = renderer,
            )
        }

    private fun renderExpression(
        expression: String,
        context: Map<String, Any>,
        renderer: EventRenderer,
    ): String {
        try {
            val m = regexToken.matchEntire(expression)
            return if (m != null) {
                val contextKey = m.groupValues[1]
                val field = m.groupValues.getOrNull(2)
                val config = m.groupValues.getOrNull(3)
                val filter = m.groupValues.getOrNull(4)
                render(
                    contextKey = contextKey,
                    field = field,
                    config = config,
                    filter = filter,
                    context = context,
                    renderer = renderer,
                )
            } else {
                throw TemplatingExpressionFormatException(expression)
            }
        } catch (ex: TemplatingException) {
            return "#error"
        }
    }

    private fun render(
        contextKey: String,
        field: String?,
        config: String?,
        filter: String?,
        context: Map<String, Any>,
        renderer: EventRenderer
    ): String {
        // Gets the context
        val contextValue = context[contextKey]
        // If no context, we need to throw an error
            ?: throw TemplatingNoContextFoundException(contextKey)
        // If the value is a project entity, we may find a specialized renderer
        val text = if (contextValue is ProjectEntity) {
            renderEntity(
                entity = contextValue,
                field = field,
                config = config,
                renderer = renderer
            )
        }
        // Else, we render as a string (if no field, config)
        else if (field.isNullOrBlank() && config.isNullOrBlank()) {
            contextValue.toString()
        }
        // Formatting error
        else {
            throw TemplatingConfiguredLiteralException(contextKey)
        }
        // Filtering
        return if (filter.isNullOrBlank()) {
            text
        } else {
            applyFilter(filter, text)
        }
    }

    private fun applyFilter(filter: String, text: String): String =
        filtersById[filter]?.apply(text)
            ?: throw TemplatingFilterNotFoundException(filter)

    private fun renderEntity(entity: ProjectEntity, field: String?, config: String?, renderer: EventRenderer): String =
        // If not field, using the entity name
        if (field.isNullOrBlank()) {
            if (config.isNullOrBlank()) {
                renderer.render(entity)
            } else {
                throw TemplatingEntityNameHavingConfigException()
            }
        }
        // Having a field, looking for a specific field renderer
        else {
            val sources = sourcesPerProjectEntityType[entity.projectEntityType]
                ?.filter { it.field == field }
                ?: emptyList()
            if (sources.isNotEmpty()) {
                if (sources.size > 1) {
                    throw TemplatingMultipleFieldSourcesException(field)
                } else {
                    val source = sources.first()
                    val configMap: Map<String, String> = if (config.isNullOrBlank()) {
                        emptyMap()
                    } else {
                        parseTemplatingConfig(config)
                    }
                    source.render(entity, configMap, renderer)
                }
            } else {
                // No field source available
                throw TemplatingNoFieldSourceException(field)
            }
        }

}