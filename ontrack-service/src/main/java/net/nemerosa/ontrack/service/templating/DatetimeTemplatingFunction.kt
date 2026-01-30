package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.common.TimeServer
import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingFunction
import net.nemerosa.ontrack.model.templating.TemplatingSourceConfig
import org.springframework.stereotype.Component
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Component
@APIDescription("Displays the current time")
@Documentation(DatetimeTemplatingFunctionParameters::class)
@DocumentationExampleCode(
    """
       #.datetime?format=yyyy-MM-dd&timezone=Europe/Brussels&days=1 
    """
)
class DatetimeTemplatingFunction(
    private val timeServer: TimeServer = Time,
) : TemplatingFunction {

    override val id: String = "datetime"

    override fun render(
        config: TemplatingSourceConfig,
        context: Map<String, Any>,
        renderer: EventRenderer,
        expressionResolver: (expression: String) -> String
    ): String {
        val format = config.getString(DatetimeTemplatingFunctionParameters::format.name) ?: "yyyy-MM-dd'T'HH:mm:ss"
        val timezone = config.getString(DatetimeTemplatingFunctionParameters::timezone.name)

        val years = config.getLong(DatetimeTemplatingFunctionParameters::years.name) ?: 0
        val months = config.getLong(DatetimeTemplatingFunctionParameters::months.name) ?: 0
        val days = config.getLong(DatetimeTemplatingFunctionParameters::days.name) ?: 0
        val hours = config.getLong(DatetimeTemplatingFunctionParameters::hours.name) ?: 0
        val minutes = config.getLong(DatetimeTemplatingFunctionParameters::minutes.name) ?: 0
        val seconds = config.getLong(DatetimeTemplatingFunctionParameters::seconds.name) ?: 0

        // Gets the actual current time
        val time = timeServer.now

        // Converting to the correct timezone
        val zonedDateTime = time.atZone(ZoneId.of("UTC")).let {
            if (timezone.isNullOrBlank()) {
                it
            } else {
                it.withZoneSameInstant(ZoneId.of(timezone))
            }
        }

        // Adjusting the time
        var actualTime = zonedDateTime
        if (years != 0L) actualTime = actualTime.plusYears(years)
        if (months != 0L) actualTime = actualTime.plusMonths(months)
        if (days != 0L) actualTime = actualTime.plusDays(days)
        if (hours != 0L) actualTime = actualTime.plusHours(hours)
        if (minutes != 0L) actualTime = actualTime.plusMinutes(minutes)
        if (seconds != 0L) actualTime = actualTime.plusSeconds(seconds)

        // Formatting the time
        val formatter = DateTimeFormatter.ofPattern(format)
        return actualTime.format(formatter)
    }

}