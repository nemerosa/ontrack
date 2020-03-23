package net.nemerosa.ontrack.model.support

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import net.nemerosa.ontrack.model.structure.ID

class IDJsonDeserializer : JsonDeserializer<ID>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ID {
        val value: Int? = p.readValueAs(Int::class.java)
        return if (value != null) ID.of(value) else ID.NONE
    }
}