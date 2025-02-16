package net.nemerosa.ontrack.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.time.Duration

class SimpleDurationDeserializer : JsonDeserializer<Duration>() {

    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): Duration {
        val s = jp.readValueAs(String::class.java)
        return parse(s)
    }

    companion object {

        const val REGEX_DURATION = "^(\\d+)([smhdw])?$"

        private val regexDuration = REGEX_DURATION.toRegex()

        fun parse(s: String): Duration =
            if (s.isBlank()) {
                Duration.ZERO
            } else {
                val mr = regexDuration.matchEntire(s)
                if (mr != null) {
                    val count = mr.groupValues[1].toLong(10)
                    val unit = mr.groupValues[2].firstOrNull()
                    when (unit) {
                        's' -> Duration.ofSeconds(count)
                        'm' -> Duration.ofMinutes(count)
                        'h' -> Duration.ofHours(count)
                        'd' -> Duration.ofDays(count)
                        'w' -> Duration.ofDays(count * 7)
                        null -> Duration.ofSeconds(count)
                        else -> error("Cannot parse the duration: $s")
                    }
                } else {
                    error("Cannot parse the duration: $s")
                }
            }
    }
}