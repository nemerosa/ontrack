package net.nemerosa.ontrack.model.support

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import net.nemerosa.ontrack.model.structure.ID

class IDJsonSerializer : JsonSerializer<ID>() {

    override fun serialize(value: ID?, jgen: JsonGenerator, provider: SerializerProvider) {
        if (value != null) {
            jgen.writeNumber(value.value)
        } else {
            jgen.writeNull()
        }
    }
}