package net.nemerosa.ontrack.extension.sonarqube.casc

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.syncForward
import net.nemerosa.ontrack.extension.casc.context.AbstractCascContext
import net.nemerosa.ontrack.extension.casc.context.SubConfigContext
import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfiguration
import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfigurationService
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.model.json.schema.JsonArrayType
import net.nemerosa.ontrack.model.json.schema.JsonType
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.json.schema.toType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * CasC definition for the list of SonarQube configurations.
 */
@Component
class SonarQubeConfigurationCasc(
    private val sonarQubeConfigurationService: SonarQubeConfigurationService,
    private val jsonTypeBuilder: JsonTypeBuilder,
) : AbstractCascContext(), SubConfigContext {

    private val logger: Logger = LoggerFactory.getLogger(SonarQubeConfigurationCasc::class.java)

    override val field: String = "sonarqube"

    override val jsonType: JsonType by lazy {
        JsonArrayType(
            description = "List of SonarQube configurations",
            items = jsonTypeBuilder.toType(SonarQubeConfiguration::class)
        )
    }

    override fun run(node: JsonNode, paths: List<String>) {
        val items = node.mapIndexed { index, child ->
            try {
                child.parseItem()
            } catch (ex: JsonParseException) {
                throw IllegalStateException(
                    "Cannot parse into ${SonarQubeConfiguration::class.qualifiedName}: ${path(paths + index.toString())}",
                    ex
                )
            }
        }

        // Gets the list of existing configurations
        val configurations = sonarQubeConfigurationService.configurations

        // Synchronization
        syncForward(
            from = items,
            to = configurations,
        ) {
            equality { a, b -> a.name == b.name }
            onCreation { item ->
                logger.info("Creating SonarQube configuration: ${item.name}")
                sonarQubeConfigurationService.newConfiguration(item)
            }
            onModification { item, _ ->
                logger.info("Updating SonarQube configuration: ${item.name}")
                sonarQubeConfigurationService.updateConfiguration(item.name, item)
            }
            onDeletion { existing ->
                logger.info("Deleting SonarQube configuration: ${existing.name}")
                sonarQubeConfigurationService.deleteConfiguration(existing.name)
            }
        }
    }

    override fun render(): JsonNode = sonarQubeConfigurationService
        .configurations
        .map(SonarQubeConfiguration::obfuscate)
        .asJson()

    private fun JsonNode.parseItem(): SonarQubeConfiguration =
        SonarQubeConfiguration(
            name = getRequiredTextField("name"),
            url = getRequiredTextField("url"),
            password = getRequiredTextField("password"),
        )
}