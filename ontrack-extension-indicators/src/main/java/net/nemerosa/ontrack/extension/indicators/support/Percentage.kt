package net.nemerosa.ontrack.extension.indicators.support

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

/**
 * Representation of a value strictly between 0 and 100.
 */
@JsonSerialize(using = PercentageJsonSerializer::class)
@JsonDeserialize(using = PercentageJsonDeserializer::class)
data class Percentage(
        val value: Int
) {

    init {
        check(value in 0..100) { "Value must be >=0 and <= 100" }
    }

    override fun toString(): String = "$value%"

    fun invert() = Percentage(100 - value)

    operator fun compareTo(percent: Percentage): Int =
            this.value.compareTo(percent.value)
}

fun Int.percent() = Percentage(this)

class PercentageJsonSerializer : JsonSerializer<Percentage>() {
    override fun serialize(value: Percentage, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeNumber(value.value)
    }
}

class PercentageJsonDeserializer : JsonDeserializer<Percentage>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Percentage =
            p.readValueAs(Int::class.java).percent()

}