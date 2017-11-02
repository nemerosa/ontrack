package net.nemerosa.ontrack.json

import com.fasterxml.jackson.databind.JsonNode

fun <T> T?.toJson(): JsonNode? =
        JsonUtils.format(this)
