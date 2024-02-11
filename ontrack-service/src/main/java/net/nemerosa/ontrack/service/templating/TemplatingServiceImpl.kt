package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.common.SimpleExpand
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.model.templating.*
import org.springframework.stereotype.Service

@Service
class TemplatingServiceImpl(
    templatingSources: List<TemplatingSource>,
    templatingFilters: List<TemplatingFilter>,
    templatingFunctions: List<TemplatingFunction>,
    private val ontrackConfigProperties: OntrackConfigProperties,
) : TemplatingService {

    private val sourcesPerProjectEntityType = ProjectEntityType.values().associateWith { type ->
        templatingSources.filter { source ->
            type in source.types
        }
    }

    private val filtersById = templatingFilters.associateBy { it.id }
    private val functionsById = templatingFunctions.associateBy { it.id }

    private val regexExpressions =
        "\\$\\{([^\\}]+)\\}".toRegex()

    @Suppress("RegExpUnnecessaryNonCapturingGroup")
    private val regexToken =
        "^([a-zA-Z_]+|#)(?:\\.([a-zA-Z_-]+))?(?:\\?((?:[a-zA-Z]+=[a-zA-Z0-9\\s,_\\.-]+)(?:&[a-zA-Z]+=[a-zA-Z0-9\\s,_\\.-]+)*))?(?:\\|([a-zA-Z_-]+))?\$".toRegex()

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
                val text = if (contextKey == "#") {
                    if (field.isNullOrBlank()) {
                        throw TemplatingMissingFunctionException()
                    } else {
                        renderFunction(
                            functionId = field,
                            config = config,
                            context = context,
                            renderer = renderer,
                        )
                    }
                } else {
                    renderContext(
                        contextKey = contextKey,
                        field = field,
                        config = config,
                        context = context,
                        renderer = renderer,
                    )
                }
                // Filtering
                return if (filter.isNullOrBlank()) {
                    text
                } else {
                    applyFilter(filter, text, renderer)
                }
            } else {
                throw TemplatingExpressionFormatException(expression)
            }
        } catch (ex: TemplatingException) {
            // Dealing with the error
            return when (ontrackConfigProperties.templating.errors) {
                OntrackConfigProperties.TemplatingErrors.IGNORE -> "#error"
                OntrackConfigProperties.TemplatingErrors.MESSAGE -> "#<${ex.message}>"
                OntrackConfigProperties.TemplatingErrors.LOGGING_STACK -> {
                    ex.printStackTrace()
                    "#<${ex.message}>"
                }

                OntrackConfigProperties.TemplatingErrors.THROW -> throw ex
            }
        }
    }

    private fun renderFunction(
        functionId: String,
        config: String?,
        context: Map<String, Any>,
        renderer: EventRenderer
    ): String {
        // Gets the function
        val function = functionsById[functionId]
            ?: throw TemplatingFunctionNotFoundException(functionId)
        // Configuration
        val configMap: Map<String, String> = if (config.isNullOrBlank()) {
            emptyMap()
        } else {
            parseTemplatingConfig(config)
        }
        // Callback
        val expressionResolver: (String) -> String = { expression: String ->
            renderExpression(expression, context, renderer)
        }
        // Rendering of the function
        return function.render(configMap, context, renderer, expressionResolver)
    }

    private fun renderContext(
        contextKey: String,
        field: String?,
        config: String?,
        context: Map<String, Any>,
        renderer: EventRenderer
    ): String {
        // Gets the context
        val contextValue = context[contextKey]
        // If no context, we need to throw an error
            ?: throw TemplatingNoContextFoundException(contextKey)
        // If the value is a project entity, we may find a specialized renderer
        return if (contextValue is ProjectEntity) {
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
    }

    private fun applyFilter(filter: String, text: String, renderer: EventRenderer): String =
        filtersById[filter]?.apply(text, renderer)
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