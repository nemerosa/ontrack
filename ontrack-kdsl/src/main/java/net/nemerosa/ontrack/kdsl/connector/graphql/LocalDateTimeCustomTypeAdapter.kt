package net.nemerosa.ontrack.kdsl.connector.graphql

import com.apollographql.apollo.api.Adapter
import com.apollographql.apollo.api.CustomScalarAdapters
import com.apollographql.apollo.api.json.JsonReader
import com.apollographql.apollo.api.json.JsonWriter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object localDateTimeCustomTypeAdapter : Adapter<LocalDateTime> {

    override fun fromJson(reader: JsonReader, customScalarAdapters: CustomScalarAdapters): LocalDateTime {
        val str = reader.nextString()
            ?: error("Cannot parse blank or null into a LocalDateTime")
        return try {
            LocalDateTime.parse(str, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (ignored: DateTimeParseException) {
            // ... with a time zone
            LocalDateTime.ofInstant(Instant.parse(str), ZoneOffset.UTC)
        }
    }

    override fun toJson(writer: JsonWriter, customScalarAdapters: CustomScalarAdapters, value: LocalDateTime) {
        val str = value.toInstant(ZoneOffset.UTC).toString()
        writer.value(str)
    }

}