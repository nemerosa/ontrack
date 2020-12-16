package net.nemerosa.ontrack.extension.indicators.model

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.form.Form

/**
 * Definition of the type of a project indicator.
 *
 * @property id Unique ID for this type.
 * @property category Associated category
 * @property name Display name for this type
 * @property link Any link describing this type in a longer form
 * @property valueType Type of the value associated with this type
 * @property valueConfig Configuration for the type of value (boundaries, thresholds, etc.)
 * @property source Where does this type come from? If `null` it means this type was entered manually
 * @property computed Flag which indicates if the associated project indicators are computed or not
 * @property deprecated Set if this type is deprecated, explaining the reason why
 */
data class IndicatorType<T, C>(
        val id: String,
        val category: IndicatorCategory,
        val name: String,
        val link: String?,
        val valueType: IndicatorValueType<T, C>,
        val valueConfig: C,
        val source: IndicatorSource?,
        val computed: Boolean,
        val deprecated: String?
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
