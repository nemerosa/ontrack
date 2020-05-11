package net.nemerosa.ontrack.extension.indicators.model

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.structure.NameDescription

data class IndicatorType<T, C>(
        val id: String,
        val category: IndicatorCategory,
        val shortName: String,
        val longName: String,
        val link: String?,
        val valueType: IndicatorValueType<T, C>,
        val valueConfig: C,
        val valueComputer: IndicatorComputer<T>?
) {
    fun toClientJson(value: T) = valueType.toClientJson(valueConfig, value)
    fun fromClientJson(value: JsonNode): T? = valueType.fromClientJson(valueConfig, value)

    fun fromStoredJson(value: JsonNode): T? = valueType.fromStoredJson(valueConfig, value)
    fun toStoredJson(value: T) = valueType.toStoredJson(valueConfig, value)

    fun toConfigClientJson(): JsonNode = valueType.toConfigClientJson(valueConfig)

    fun getUpdateForm(value: T?): Form = valueType.form(
            nameDescription = toNameDescription(),
            config = valueConfig,
            value = value
    )

    private fun toNameDescription() = NameDescription(
            shortName,
            longName
    )

    fun getStatus(value: T) = valueType.status(valueConfig, value)

}
