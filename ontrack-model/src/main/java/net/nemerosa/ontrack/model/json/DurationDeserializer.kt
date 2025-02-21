package net.nemerosa.ontrack.model.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import net.nemerosa.ontrack.common.parseDuration
import java.time.Duration

class DurationDeserializer : JsonDeserializer<Duration>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Duration {
        val value: String = p.readValueAs(String::class.java)
        return parseDuration(value)
            ?: error("Cannot parse duration: $value")
    }
}