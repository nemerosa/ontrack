package net.nemerosa.ontrack.model.json.schema

import net.nemerosa.ontrack.common.durationRegex

class JsonDurationType(
    description: String?,
) : AbstractJsonBaseType("string", description) {
    val pattern: String = durationRegex
}