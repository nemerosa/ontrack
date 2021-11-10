package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import net.nemerosa.ontrack.json.parse

object ConfigParser {

    private val yamlFactory = YAMLFactory().apply {
        enable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE)
    }

    private val mapper = ObjectMapper(yamlFactory)

    fun parseYaml(yaml: String): IngestionConfig? =
        try {
            mapper.readTree(yaml).parse()
        } catch (_: Exception) {
            null // Ignoring any parsing exception
        }

}