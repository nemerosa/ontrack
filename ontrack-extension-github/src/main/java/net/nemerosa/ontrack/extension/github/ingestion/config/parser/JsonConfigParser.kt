package net.nemerosa.ontrack.extension.github.ingestion.config.parser

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfig

interface JsonConfigParser {

    fun parse(json: JsonNode): IngestionConfig

}