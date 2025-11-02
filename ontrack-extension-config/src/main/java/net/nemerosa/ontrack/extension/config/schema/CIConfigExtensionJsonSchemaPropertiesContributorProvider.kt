package net.nemerosa.ontrack.extension.config.schema

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.config.extensions.CIConfigExtension
import net.nemerosa.ontrack.model.json.schema.JsonSchemaPropertiesContributorProvider
import net.nemerosa.ontrack.model.json.schema.JsonType
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class CIConfigExtensionJsonSchemaPropertiesContributorProvider(
    private val extensionManager: ExtensionManager,
) : JsonSchemaPropertiesContributorProvider {

    private val ciExtensions: List<CIConfigExtension<*>> by lazy {
        extensionManager.getExtensions(CIConfigExtension::class.java).toList()
    }

    override fun contributeProperties(configuration: String, jsonTypeBuilder: JsonTypeBuilder): Map<String, JsonType> {
        val type = ProjectEntityType.valueOf(configuration)
        val extensions = ciExtensions.filter {
            type in it.projectEntityTypes
        }
        return extensions.associate {
            it.id to it.createJsonType(jsonTypeBuilder)
        }
    }
}