package net.nemerosa.ontrack.kdsl.connector.graphql

import com.apollographql.apollo.api.CustomTypeAdapter
import com.apollographql.apollo.api.CustomTypeValue
import com.apollographql.apollo.api.ScalarType
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class LocalDateTimeCustomTypeAdapter : CustomTypeAdapter<LocalDateTime> {

    override fun decode(value: CustomTypeValue<*>): LocalDateTime {
        val s = value.value?.toString()?.takeIf { it.isNotBlank() }
            ?: error("Cannot parse blank or null into a LocalDateTime")
        return try {
            LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (ignored: DateTimeParseException) {
            // ... with a time zone
            LocalDateTime.ofInstant(Instant.parse(s), ZoneOffset.UTC)
        }
    }

    override fun encode(value: LocalDateTime): CustomTypeValue<*> {
        return CustomTypeValue.GraphQLString(value.toInstant(ZoneOffset.UTC).toString())
    }

    companion object {
        val TYPE = object : ScalarType {
            override fun typeName(): String = "LocalDateTime"
            override fun className(): String = LocalDateTime::class.java.name
        }
    }

}