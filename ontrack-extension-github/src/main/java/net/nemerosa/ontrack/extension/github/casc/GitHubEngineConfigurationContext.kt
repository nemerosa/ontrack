package net.nemerosa.ontrack.extension.github.casc

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.syncForward
import net.nemerosa.ontrack.extension.casc.context.AbstractCascContext
import net.nemerosa.ontrack.extension.casc.context.SubConfigContext
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.json.getTextField
import net.nemerosa.ontrack.model.json.schema.JsonArrayType
import net.nemerosa.ontrack.model.json.schema.JsonType
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.json.schema.toType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * CasC definition for the list of GitHub configurations.
 */
@Component
class GitHubEngineConfigurationContext(
    private val gitHubConfigurationService: GitHubConfigurationService,
    private val jsonTypeBuilder: JsonTypeBuilder,
) : AbstractCascContext(), SubConfigContext {

    private val logger: Logger = LoggerFactory.getLogger(GitHubEngineConfigurationContext::class.java)

    override val field: String = "github"

    override val jsonType: JsonType by lazy {
        JsonArrayType(
            description = "List of GitHub configurations",
            items = jsonTypeBuilder.toType(GitHubEngineConfiguration::class)
        )
    }

    override fun run(node: JsonNode, paths: List<String>) {
        val items = node.mapIndexed { index, child ->
            try {
                child.parseItem()
            } catch (ex: JsonParseException) {
                throw IllegalStateException(
                    "Cannot parse into ${GitHubEngineConfiguration::class.qualifiedName}: ${path(paths + index.toString())}",
                    ex
                )
            }
        }

        // Gets the list of existing configurations
        val configurations = gitHubConfigurationService.configurations

        // Synchronization
        syncForward(
            from = items,
            to = configurations,
        ) {
            equality { a, b -> a.name == b.name }
            onCreation { item ->
                logger.info("Creating GitHub configuration: ${item.name}")
                gitHubConfigurationService.newConfiguration(item)
            }
            onModification { item, _ ->
                logger.info("Updating GitHub configuration: ${item.name}")
                gitHubConfigurationService.updateConfiguration(item.name, item)
            }
            onDeletion { existing ->
                logger.info("Deleting GitHub configuration: ${existing.name}")
                gitHubConfigurationService.deleteConfiguration(existing.name)
            }
        }
    }

    override fun render(): JsonNode = gitHubConfigurationService
        .configurations
        .map(GitHubEngineConfiguration::obfuscate)
        .asJson()

    private fun JsonNode.parseItem(): GitHubEngineConfiguration =
        GitHubEngineConfiguration(
            name = getRequiredTextField("name"),
            url = getTextField("url"),
            user = getTextField("user"),
            password = getTextField("password"),
            oauth2Token = getTextField("token"),
            appId = getTextField("app-id"),
            appPrivateKey = getTextField("app-private-key"),
            appInstallationAccountName = getTextField("app-installation"),
            autoMergeToken = getTextField("auto-merge-token"),
        )
}