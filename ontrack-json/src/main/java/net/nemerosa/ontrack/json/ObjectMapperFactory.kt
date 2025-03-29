package net.nemerosa.ontrack.json

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.time.*

object ObjectMapperFactory {

    private val JSON_MODULE_VERSION: Version = Version(
        1,
        0,
        0,
        null,
        "net.nemerosa.ontrack",
        "ontrack-json"
    )

    fun create(): ObjectMapper {
        val mapper = ObjectMapper()
        // Support for JDK 8 times
        jdkTime(mapper)
        // Support for Kotlin
        mapper.registerModule(KotlinModule.Builder().build())
        // OK
        return mapper
    }

    fun create(viewClass: Class<*>?): ObjectMapper {
        return CustomObjectMapper(viewClass)
    }

    private fun jdkTime(mapper: ObjectMapper) {
        val jdkTimeModule = SimpleModule(
            "JDKTimeModule",
            JSON_MODULE_VERSION
        )
        // LocalDateTime
        jdkTimeModule.addSerializer(LocalDateTime::class.java, JDKLocalDateTimeSerializer())
        jdkTimeModule.addDeserializer(LocalDateTime::class.java, JDKLocalDateTimeDeserializer())
        // LocalTime
        jdkTimeModule.addSerializer(LocalTime::class.java, JDKLocalTimeSerializer())
        jdkTimeModule.addDeserializer(LocalTime::class.java, JDKLocalTimeDeserializer())
        // LocalDate
        jdkTimeModule.addSerializer(LocalDate::class.java, JDKLocalDateSerializer())
        jdkTimeModule.addDeserializer(LocalDate::class.java, JDKLocalDateDeserializer())
        // YearMonth
        jdkTimeModule.addSerializer(YearMonth::class.java, JDKYearMonthSerializer())
        jdkTimeModule.addDeserializer(YearMonth::class.java, JDKYearMonthDeserializer())
        // Support for durations
        jdkTimeModule.addSerializer(Duration::class.java, DurationSerializer.INSTANCE)
        jdkTimeModule.addDeserializer(Duration::class.java, DurationDeserializer.INSTANCE)
        // OK
        mapper.registerModule(jdkTimeModule)
    }

    private class CustomObjectMapper(viewClass: Class<*>?) : ObjectMapper() {
        init {
            this._serializationConfig = _serializationConfig.withView(viewClass)
        }
    }
}
