package net.nemerosa.ontrack.extension.av.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator

object AutoVersioningConfigParser {

    private val yamlFactory = YAMLFactory().apply {
        enable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE)
        disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
    }

    private val mapper = ObjectMapper(yamlFactory)

    /**
     * Renders a configuration as YAML, omitting default fields
     */
    fun toYaml(config: AutoVersioningConfig) =
        mapper.writeValueAsString(config)
}