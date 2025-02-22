package net.nemerosa.ontrack.extension.bitbucket.cloud.casc

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.syncForward
import net.nemerosa.ontrack.extension.bitbucket.cloud.configuration.BitbucketCloudConfiguration
import net.nemerosa.ontrack.extension.bitbucket.cloud.configuration.BitbucketCloudConfigurationService
import net.nemerosa.ontrack.extension.casc.context.AbstractCascContext
import net.nemerosa.ontrack.extension.casc.context.SubConfigContext
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.json.schema.JsonArrayType
import net.nemerosa.ontrack.model.json.schema.JsonType
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.json.schema.toType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class BitbucketCloudConfigurationCascContext(
    private val bitbucketCloudConfigurationService: BitbucketCloudConfigurationService,
    private val jsonTypeBuilder: JsonTypeBuilder,
) : AbstractCascContext(), SubConfigContext {

    private val logger: Logger = LoggerFactory.getLogger(BitbucketCloudConfigurationCascContext::class.java)

    override val field: String = "bitbucket-cloud"

    override val jsonType: JsonType by lazy {
        JsonArrayType(
            description = "List of Bitbucket Cloud configurations",
            items = jsonTypeBuilder.toType(BitbucketCloudConfigurationCascData::class)
        )
    }

    override fun run(node: JsonNode, paths: List<String>) {
        val items = node.mapIndexed { index, child ->
            try {
                child.parse<BitbucketCloudConfigurationCascData>()
            } catch (ex: JsonParseException) {
                throw IllegalStateException(
                    "Cannot parse into ${BitbucketCloudConfiguration::class.qualifiedName}: ${path(paths + index.toString())}",
                    ex
                )
            }
        }

        // Gets the list of existing configurations
        val configs: List<BitbucketCloudConfiguration> = bitbucketCloudConfigurationService.configurations

        // Synchronization
        syncForward(
            from = items,
            to = configs,
        ) {
            equality { a, b -> a.name == b.name }
            onCreation { item ->
                logger.info("Creating Bitbucket Cloud configuration: ${item.name}")
                bitbucketCloudConfigurationService.newConfiguration(item.toConfiguration())
            }
            onModification { item, _ ->
                logger.info("Updating Bitbucket Cloud configuration: ${item.name}")
                bitbucketCloudConfigurationService.updateConfiguration(item.name, item.toConfiguration())
            }
            onDeletion { existing ->
                logger.info("Deleting Bitbucket Cloud configuration: ${existing.name}")
                bitbucketCloudConfigurationService.deleteConfiguration(existing.name)
            }
        }
    }

    override fun render(): JsonNode = bitbucketCloudConfigurationService.configurations.map {
        mapOf(
            "name" to it.name,
            "workspace" to it.workspace,
            "user" to it.user,
            "password" to "",
        )
    }.asJson()

    data class BitbucketCloudConfigurationCascData(
        @APIDescription("Name of the configuration")
        val name: String,
        @APIDescription("Slug of the Bitbucket Cloud workspace to connect to")
        val workspace: String,
        @APIDescription("Name of the user used to connect to Bitbucket Cloud")
        val user: String,
        @APIDescription("App password used to connect to Bitbucket Cloud")
        val password: String?,
    ) {
        fun toConfiguration() = BitbucketCloudConfiguration(
            name = name,
            workspace = workspace,
            user = user,
            password = password,
        )
    }

}