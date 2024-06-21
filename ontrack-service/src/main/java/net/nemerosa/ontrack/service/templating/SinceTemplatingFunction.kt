package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingFunction
import net.nemerosa.ontrack.model.templating.TemplatingMisconfiguredConfigParamException
import net.nemerosa.ontrack.model.templating.getRequiredTemplatingParam
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime

@Component
@APIDescription("Renders a period of time")
@Documentation(SinceTemplatingFunctionParameters::class)
@DocumentationExampleCode(
    """
       #.since?from=workflowInfo.start 
    """
)
class SinceTemplatingFunction : TemplatingFunction {

    override fun render(
        configMap: Map<String, String>,
        context: Map<String, Any>,
        renderer: EventRenderer,
        expressionResolver: (expression: String) -> String
    ): String {
        val format = configMap[SinceTemplatingFunctionParameters::format.name] ?: FORMAT_SECONDS
        if (format != FORMAT_SECONDS && format != FORMAT_MILLIS) {
            throw TemplatingMisconfiguredConfigParamException(
                SinceTemplatingFunctionParameters::format.name,
                "Must be seconds or millis."
            )
        }
        val fromExpression = configMap.getRequiredTemplatingParam(SinceTemplatingFunctionParameters::from.name)
        val refExpression = configMap[SinceTemplatingFunctionParameters::ref.name]

        val fromText = expressionResolver(fromExpression)
        val refText = refExpression?.run { expressionResolver(refExpression) }

        val from = parseTime(fromText)
        val ref = refText?.takeIf { it.isNotBlank() }?.run { parseTime(this) } ?: Time.now

        val duration: Duration = Duration.between(from, ref)

        return when (format) {
            FORMAT_SECONDS -> duration.toSeconds().toString()
            FORMAT_MILLIS -> duration.toMillis().toString()
            else -> error("Unknown format: $format")
        }
    }

    private fun parseTime(text: String): LocalDateTime =
        if (text.isBlank()) {
            error("Cannot parse blank text into a date/time")
        } else {
            Time.fromStorage(text) ?: error("Cannot parse text into a date/time")
        }

    override val id: String = "since"

    companion object {
        private const val FORMAT_SECONDS = "seconds"
        private const val FORMAT_MILLIS = "millis"
    }

}