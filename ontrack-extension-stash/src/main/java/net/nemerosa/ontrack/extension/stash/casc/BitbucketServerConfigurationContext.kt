package net.nemerosa.ontrack.extension.stash.casc

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.syncForward
import net.nemerosa.ontrack.extension.casc.context.AbstractCascContext
import net.nemerosa.ontrack.extension.casc.context.SubConfigContext
import net.nemerosa.ontrack.extension.casc.schema.*
import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import net.nemerosa.ontrack.extension.stash.service.StashConfigurationService
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.getRequiredTextField
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * CasC definition for the list of Bitbucket Server configurations.
 */
@Component
class BitbucketServerConfigurationContext(
    private val stashConfigurationService: StashConfigurationService,
) : AbstractCascContext(), SubConfigContext {

    private val logger: Logger = LoggerFactory.getLogger(BitbucketServerConfigurationContext::class.java)

    override val field: String = "bitbucketServer"

    override val type: CascType
        get() = cascArray(
            "List of Bitbucket Server configurations",
            cascObject(
                "Bitbucket Server configuration",
                cascField("name", cascString, "Unique name for the configuration", required = true),
                cascField("url", cascString, "Bitbucket Server root URL", required = true),
                cascField("user", cascString, "Bitbucket Server user", required = true),
                cascField("password", cascString, "Bitbucket Server password or token", required = true),
            )
        )

    override fun run(node: JsonNode, paths: List<String>) {
        val items = node.mapIndexed { index, child ->
            try {
                child.parseItem()
            } catch (ex: JsonParseException) {
                throw IllegalStateException(
                    "Cannot parse into ${StashConfiguration::class.qualifiedName}: ${path(paths + index.toString())}",
                    ex
                )
            }
        }

        // Gets the list of existing configurations
        val configurations = stashConfigurationService.configurations

        // Synchronization
        syncForward(
            from = items,
            to = configurations,
        ) {
            equality { a, b -> a.name == b.name }
            onCreation { item ->
                logger.info("Creating GitHub configuration: ${item.name}")
                stashConfigurationService.newConfiguration(item)
            }
            onModification { item, _ ->
                logger.info("Updating GitHub configuration: ${item.name}")
                stashConfigurationService.updateConfiguration(item.name, item)
            }
            onDeletion { existing ->
                logger.info("Deleting GitHub configuration: ${existing.name}")
                stashConfigurationService.deleteConfiguration(existing.name)
            }
        }
    }

    override fun render(): JsonNode = stashConfigurationService
        .configurations
        .map(StashConfiguration::obfuscate)
        .asJson()

    private fun JsonNode.parseItem(): StashConfiguration =
        StashConfiguration(
            name = getRequiredTextField("name"),
            url = getRequiredTextField("url"),
            user = getRequiredTextField("user"),
            password = getRequiredTextField("password"),
        )
}