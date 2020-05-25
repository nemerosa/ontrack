package net.nemerosa.ontrack.extension.indicators.model

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.form.Form

interface IndicatorValueType<T, C> : Extension {

    /**
     * Display name
     */
    val name: String

    fun form(config: C, value: T?): Form

    fun status(config: C, value: T): IndicatorCompliance

    fun toClientJson(config: C, value: T): JsonNode
    fun fromClientJson(config: C, value: JsonNode): T?

    fun fromStoredJson(valueConfig: C, value: JsonNode): T?
    fun toStoredJson(config: C, value: T): JsonNode

    fun configForm(config: C?): Form
    fun toConfigForm(config: C): JsonNode
    fun fromConfigForm(config: JsonNode): C

    fun toConfigClientJson(config: C): JsonNode
    fun toConfigStoredJson(config: C): JsonNode
    fun fromConfigStoredJson(config: JsonNode): C

}

/**
 * ID if the FQCN of the value type.
 */
val IndicatorValueType<*, *>.id: String get() = this::class.java.name
