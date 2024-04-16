package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.common.TimeServer
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingFunction
import org.springframework.stereotype.Component
import java.time.Duration
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
        configMap: Map<String, String>,
        context: Map<String, Any>,
        renderer: EventRenderer,
        expressionResolver: (expression: String) -> String
    ): String {
        val format = configMap[DatetimeTemplatingFunctionParameters::format.name] ?: "yyyy-MM-dd'T'HH:mm:ss"
        val timezone = configMap[DatetimeTemplatingFunctionParameters::timezone.name]

        val years = configMap[DatetimeTemplatingFunctionParameters::years.name]?.toLong() ?: 0
        val months = configMap[DatetimeTemplatingFunctionParameters::months.name]?.toLong() ?: 0
        val days = configMap[DatetimeTemplatingFunctionParameters::days.name]?.toLong() ?: 0
        val hours = configMap[DatetimeTemplatingFunctionParameters::hours.name]?.toLong() ?: 0
        val minutes = configMap[DatetimeTemplatingFunctionParameters::minutes.name]?.toLong() ?: 0
        val seconds = configMap[DatetimeTemplatingFunctionParameters::seconds.name]?.toLong() ?: 0

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