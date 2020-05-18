package net.nemerosa.ontrack.extension.indicators.model

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.form.Form

data class IndicatorType<T, C>(
        val id: String,
        val category: IndicatorCategory,
        val name: String,
        val link: String?,
        val valueType: IndicatorValueType<T, C>,
        val valueConfig: C,
        val source: IndicatorSource?
) {
    fun toClientJson(value: T) = valueType.toClientJson(valueConfig, value)
    fun fromClientJson(value: JsonNode): T? = valueType.fromClientJson(valueConfig, value)

    fun fromStoredJson(value: JsonNode): T? = valueType.fromStoredJson(valueConfig, value)
    fun toStoredJson(value: T) = valueType.toStoredJson(valueConfig, value)

    fun toConfigClientJson(): JsonNode = valueType.toConfigClientJson(valueConfig)

    fun getUpdateForm(value: T?): Form = valueType.form(
            config = valueConfig,
            value = value
    )

    fun getStatus(value: T) = valueType.status(valueConfig, value)

}
