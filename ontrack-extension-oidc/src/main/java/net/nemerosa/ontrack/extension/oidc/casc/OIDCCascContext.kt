package net.nemerosa.ontrack.extension.oidc.casc

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.casc.context.AbstractCascContext
import net.nemerosa.ontrack.casc.context.SubConfigContext
import net.nemerosa.ontrack.casc.schema.CascType
import net.nemerosa.ontrack.casc.schema.cascObject
import net.nemerosa.ontrack.common.syncForward
import net.nemerosa.ontrack.extension.oidc.settings.OIDCSettingsService
import net.nemerosa.ontrack.extension.oidc.settings.OntrackOIDCProvider
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.json.getTextField
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
        get() = cascObject(OntrackOIDCProvider::class)

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

    private fun JsonNode.parseItem() = OntrackOIDCProvider(
        id = getRequiredTextField("id"),
        name = getRequiredTextField("name"),
        description = getTextField("description") ?: "",
        issuerId = getRequiredTextField("issuer-id"),
        clientId = getRequiredTextField("client-id"),
        clientSecret = getTextField("client-secret") ?: "",
        groupFilter = getTextField("group-filter"),
    )

}