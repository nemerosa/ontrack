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

        private val regexDuration = "^(\\d+)[hdw]$".toRegex()
        private val regexDays = "^(\\d+)$".toRegex()

        fun parse(s: String): Duration =
            if (s.isBlank()) {
                Duration.ZERO
            } else {
                val mr = regexDuration.matchEntire(s)
                if (mr != null) {
                    val count = mr.groupValues[1].toLong(10)
                    when (s.last()) {
                        'h' -> Duration.ofHours(count)
                        'd' -> Duration.ofDays(count)
                        'w' -> Duration.ofDays(count * 7)
                        else -> error("Cannot parse the duration: $s")
                    }
                } else {
                    val md = regexDays.matchEntire(s)
                    if (md != null) {
                        val count = s.toLong(10)
                        Duration.ofDays(count)
                    } else {
                        error("Cannot parse the duration: $s")
                    }
                }
            }
    }
}