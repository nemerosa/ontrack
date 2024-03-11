package net.nemerosa.ontrack.extension.jira.casc

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.syncForward
import net.nemerosa.ontrack.extension.casc.context.AbstractCascContext
import net.nemerosa.ontrack.extension.casc.context.SubConfigContext
import net.nemerosa.ontrack.extension.casc.schema.*
import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.JIRAConfigurationService
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.getListStringField
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * CasC definition for the list of JIRA configurations.
 */
@Component
class JIRAConfigurationCasc(
    private val jiraConfigurationService: JIRAConfigurationService,
) : AbstractCascContext(), SubConfigContext {

    private val logger: Logger = LoggerFactory.getLogger(JIRAConfigurationCasc::class.java)

    override val field: String = "jira"

    override val type: CascType
        get() = cascArray(
            "List of JIRA configurations",
            cascObject(
                "JIRA configuration",
                cascField("name", cascString, "Unique name for the configuration", required = true),
                cascField("url", cascString, "JIRA root URL", required = true),
                cascField("user", cascString, "JIRA user", required = true),
                cascField("password", cascString, "JIRA password or token", required = true),
                cascField(
                    name = JIRAConfiguration::include.name,
                    type = cascArray(
                        "List of regular expressions",
                        cascString
                    ),
                    description = getPropertyDescription(JIRAConfiguration::include),
                    required = false,
                ),
                cascField(
                    name = JIRAConfiguration::exclude.name,
                    type = cascArray(
                        "List of regular expressions",
                        cascString
                    ),
                    description = getPropertyDescription(JIRAConfiguration::exclude),
                    required = false,
                ),
            )
        )

    override fun run(node: JsonNode, paths: List<String>) {
        val items = node.mapIndexed { index, child ->
            try {
                child.parseItem()
            } catch (ex: JsonParseException) {
                throw IllegalStateException(
                    "Cannot parse into ${JIRAConfiguration::class.qualifiedName}: ${path(paths + index.toString())}",
                    ex
                )
            }
        }

        // Gets the list of existing configurations
        val configurations = jiraConfigurationService.configurations

        // Synchronization
        syncForward(
            from = items,
            to = configurations,
        ) {
            equality { a, b -> a.name == b.name }
            onCreation { item ->
                logger.info("Creating JIRA configuration: ${item.name}")
                jiraConfigurationService.newConfiguration(item)
            }
            onModification { item, _ ->
                logger.info("Updating JIRA configuration: ${item.name}")
                jiraConfigurationService.updateConfiguration(item.name, item)
            }
            onDeletion { existing ->
                logger.info("Deleting JIRA configuration: ${existing.name}")
                jiraConfigurationService.deleteConfiguration(existing.name)
            }
        }
    }

    override fun render(): JsonNode = jiraConfigurationService
        .configurations
        .map(JIRAConfiguration::obfuscate)
        .asJson()

    private fun JsonNode.parseItem(): JIRAConfiguration =
        JIRAConfiguration(
            name = getRequiredTextField("name"),
            url = getRequiredTextField("url"),
            user = getRequiredTextField("user"),
            password = getRequiredTextField("password"),
            include = getListStringField("include") ?: emptyList(),
            exclude = getListStringField("exclude") ?: emptyList(),
        )

}