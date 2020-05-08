package net.nemerosa.ontrack.extension.indicators.model

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.structure.NameDescription

interface IndicatorValueType<T, C> {

    fun form(nameDescription: NameDescription, config: C, value: T?): Form

    fun status(config: C, value: T): IndicatorStatus

    fun toClientJson(config: C, value: T): JsonNode

    fun fromStoredJson(valueConfig: C, value: JsonNode): T?

}
