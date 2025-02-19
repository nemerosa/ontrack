package net.nemerosa.ontrack.extension.jenkins.casc

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.syncForward
import net.nemerosa.ontrack.extension.casc.context.AbstractCascContext
import net.nemerosa.ontrack.extension.casc.context.SubConfigContext
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfiguration
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfigurationService
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.json.getTextField
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.json.schema.JsonArrayType
import net.nemerosa.ontrack.model.json.schema.JsonType
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.json.schema.toType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class JenkinsConfigurationCascContext(
    private val jenkinsConfigurationService: JenkinsConfigurationService,
    private val jsonTypeBuilder: JsonTypeBuilder,
) : AbstractCascContext(), SubConfigContext {

    private val logger: Logger = LoggerFactory.getLogger(JenkinsConfigurationCascContext::class.java)

    override val field: String = "jenkins"

    override val jsonType: JsonType by lazy {
        JsonArrayType(
            description = "List of Jenkins configurations",
            items = jsonTypeBuilder.toType(JenkinsConfigurationCasc::class),
        )
    }

    override fun run(node: JsonNode, paths: List<String>) {
        val items = node.mapIndexed { index, child ->
            try {
                child.parseItem()
            } catch (ex: JsonParseException) {
                throw IllegalStateException(
                    "Cannot parse into ${JenkinsConfiguration::class.qualifiedName}: ${path(paths + index.toString())}",
                    ex
                )
            }
        }

        // Gets the list of existing configurations
        val configurations = jenkinsConfigurationService.configurations

        // Synchronization
        syncForward(
            from = items,
            to = configurations,
        ) {
            equality { a, b -> a.name == b.name }
            onCreation { item ->
                logger.info("Creating Jenkins configuration: ${item.name}")
                jenkinsConfigurationService.newConfiguration(item)
            }
            onModification { item, _ ->
                logger.info("Updating Jenkins configuration: ${item.name}")
                jenkinsConfigurationService.updateConfiguration(item.name, item)
            }
            onDeletion { existing ->
                logger.info("Deleting Jenkins configuration: ${existing.name}")
                jenkinsConfigurationService.deleteConfiguration(existing.name)
            }
        }
    }

    override fun render(): JsonNode = jenkinsConfigurationService
        .configurations
        .map(JenkinsConfiguration::obfuscate)
        .asJson()

    private fun JsonNode.parseItem(): JenkinsConfiguration =
        JenkinsConfiguration(
            name = getRequiredTextField(JenkinsConfigurationCasc::name.name),
            url = getRequiredTextField(JenkinsConfigurationCasc::url.name),
            user = getTextField(JenkinsConfigurationCasc::user.name),
            password = getTextField(JenkinsConfigurationCasc::password.name),
        )

    @APIDescription("Jenkins configuration")
    data class JenkinsConfigurationCasc(
        @APIDescription("Unique name for this configuration")
        val name: String,
        @APIDescription("URL to the Jenkins instance")
        val url: String,
        @APIDescription("Username used to connect to Jenkins")
        val user: String?,
        @APIDescription("Password used to connect to Jenkins")
        val password: String?,
    )
}