package net.nemerosa.ontrack.extension.av.settings

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.time.Duration

class AutoVersioningSettingsDurationDeserializer : JsonDeserializer<Duration>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Duration {
        val seconds: Long = p.readValueAs(Long::class.java)
        return Duration.ofSeconds(seconds)
    }

}