package net.nemerosa.ontrack.extension.config.model

import org.springframework.stereotype.Component

@Component
class CIConfigurationParserImpl : CIConfigurationParser {
    override fun parseConfig(yaml: String): RootConfiguration {
        TODO("Parsing of CI configurations is not yet implemented")
    }
}