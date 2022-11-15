package net.nemerosa.ontrack.extension.github.ingestion.config.parser

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfig
import net.nemerosa.ontrack.json.parse

object ConfigV1Parser : AbstractJsonConfigParser() {
    override fun parse(json: JsonNode): IngestionConfig =
        json.parse()
}