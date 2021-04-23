package net.nemerosa.ontrack.extension.oidc.casc

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import net.nemerosa.ontrack.common.syncForward
import net.nemerosa.ontrack.extension.casc.context.AbstractCascContext
import net.nemerosa.ontrack.extension.casc.context.SubConfigContext
import net.nemerosa.ontrack.extension.casc.schema.CascType
import net.nemerosa.ontrack.extension.casc.schema.cascArray
import net.nemerosa.ontrack.extension.casc.schema.cascObject
import net.nemerosa.ontrack.extension.oidc.settings.OIDCSettingsService
import net.nemerosa.ontrack.extension.oidc.settings.OntrackOIDCProvider
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OIDCCascContext(
    private val oidcSettingsService: OIDCSettingsService,
) : AbstractCascContext(), SubConfigContext {

    private val logger: Logger = LoggerFactory.getLogger(OIDCCascContext::class.java)

    override val field: String = "oidc"

    override val type: CascType
        get() = cascArray(
            "List of OIDC providers",
            cascObject(OntrackOIDCProvider::class)
        )

    override fun run(node: JsonNode, paths: List<String>) {
        val items = node.mapIndexed { index, child ->
            try {
                child.parseItem()
            } catch (ex: JsonParseException) {
                throw IllegalStateException(
                    "Cannot parse into ${OntrackOIDCProvider::class.qualifiedName}: ${path(paths + index.toString())}",
                    ex
                )
            }
        }
        // Gets the list of existing providers
        val providers = oidcSettingsService.providers

        // Synchronization
        syncForward(
            from = items,
            to = providers,
        ) {
            equality { a, b -> a.id == b.id }
            onCreation { item ->
                logger.info("Creating OIDC provider: ${item.id}")
                oidcSettingsService.createProvider(item)
            }
            onModification { item, _ ->
                logger.info("Updating OIDC provider: ${item.id}")
                oidcSettingsService.updateProvider(item)
            }
            onDeletion { existing ->
                logger.info("Deleting OIDC provider: ${existing.id}")
                oidcSettingsService.deleteProvider(existing.id)
            }
        }
    }

    override fun render(): JsonNode = oidcSettingsService.providers.asJson()

    private fun JsonNode.parseItem(): OntrackOIDCProvider {
        if (this is ObjectNode && !has(OntrackOIDCProvider::clientSecret.name)) {
            put(OntrackOIDCProvider::clientSecret.name, "")
        }
        return parse()
    }

}