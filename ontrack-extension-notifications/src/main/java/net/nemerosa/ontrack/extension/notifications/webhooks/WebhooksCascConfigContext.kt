package net.nemerosa.ontrack.extension.notifications.webhooks

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.syncForward
import net.nemerosa.ontrack.extension.casc.context.AbstractCascContext
import net.nemerosa.ontrack.extension.casc.context.SubConfigContext
import net.nemerosa.ontrack.extension.casc.schema.CascType
import net.nemerosa.ontrack.extension.casc.schema.cascArray
import net.nemerosa.ontrack.extension.casc.schema.cascObject
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.asJson
import org.springframework.stereotype.Component

@Component
class WebhooksCascConfigContext(
    private val webhookAdminService: WebhookAdminService,
) : AbstractCascContext(), SubConfigContext {

    override val field: String = "webhooks"

    override val type: CascType = cascArray(
        "List of webhooks",
        cascObject(Webhook::class)
    )

    override fun run(node: JsonNode, paths: List<String>) {
//        val items = node.mapIndexed { index, child ->
//            try {
//                child.parse<Webhook>
//            } catch (ex: JsonParseException) {
//                throw IllegalStateException(
//                    "Cannot parse into ${OntrackOIDCProvider::class.qualifiedName}: ${path(paths + index.toString())}",
//                    ex
//                )
//            }
//        }
//        // Gets the list of existing providers
//        val providers = oidcSettingsService.providers
//
//        // Synchronization
//        syncForward(
//            from = items,
//            to = providers,
//        ) {
//            equality { a, b -> a.id == b.id }
//            onCreation { item ->
//                logger.info("Creating OIDC provider: ${item.id}")
//                oidcSettingsService.createProvider(item)
//            }
//            onModification { item, _ ->
//                logger.info("Updating OIDC provider: ${item.id}")
//                oidcSettingsService.updateProvider(item)
//            }
//            onDeletion { existing ->
//                logger.info("Deleting OIDC provider: ${existing.id}")
//                oidcSettingsService.deleteProvider(existing.id)
//            }
//        }
    }

    override fun render(): JsonNode =
        webhookAdminService.webhooks.asJson()

}