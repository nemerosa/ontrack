package net.nemerosa.ontrack.extension.indicators.store

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.indicators.model.IndicatorStatus
import net.nemerosa.ontrack.model.structure.Signature

class StoredIndicator(
        val value: JsonNode,
        val status: IndicatorStatus?,
        val comment: String?,
        val signature: Signature
)