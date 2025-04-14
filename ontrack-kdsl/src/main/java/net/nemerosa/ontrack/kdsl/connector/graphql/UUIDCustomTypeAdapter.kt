package net.nemerosa.ontrack.kdsl.connector.graphql

import com.apollographql.apollo.api.Adapter
import com.apollographql.apollo.api.CustomScalarAdapters
import com.apollographql.apollo.api.json.JsonReader
import com.apollographql.apollo.api.json.JsonWriter
import java.util.*

object uuidCustomTypeAdapter : Adapter<UUID> {

    override fun fromJson(reader: JsonReader, customScalarAdapters: CustomScalarAdapters): UUID {
        val str = reader.nextString()
            ?: error("Cannot parse blank or null into a UUID")
        return UUID.fromString(str)
    }

    override fun toJson(writer: JsonWriter, customScalarAdapters: CustomScalarAdapters, value: UUID) {
        writer.value(value.toString())
    }

}