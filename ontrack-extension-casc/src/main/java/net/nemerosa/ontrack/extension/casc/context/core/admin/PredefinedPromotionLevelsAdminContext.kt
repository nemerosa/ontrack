package net.nemerosa.ontrack.extension.casc.context.core.admin

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.syncForward
import net.nemerosa.ontrack.extension.casc.context.AbstractCascContext
import net.nemerosa.ontrack.extension.casc.schema.CascType
import net.nemerosa.ontrack.extension.casc.schema.cascArray
import net.nemerosa.ontrack.extension.casc.schema.cascField
import net.nemerosa.ontrack.extension.casc.schema.cascObject
import net.nemerosa.ontrack.extension.scm.service.SCMRefService
import net.nemerosa.ontrack.extension.scm.service.downloadDocument
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PredefinedPromotionLevelsAdminContext(
    private val predefinedPromotionLevelService: PredefinedPromotionLevelService,
    private val scmRefService: SCMRefService,
) : AbstractCascContext(), SubAdminContext {

    private val logger: Logger = LoggerFactory.getLogger(PredefinedPromotionLevelsAdminContext::class.java)

    override val field: String = "predefined-promotion-levels"

    override val type: CascType = cascObject(
        description = "Predefined promotion levels",
        cascField(PredefinedPromotionLevelsAdminContextType::replace),
        cascField(
            name = "list",
            description = "List of promotion levels",
            required = true,
            type = cascArray(
                description = "List of promotion levels",
                type = cascObject(PredefinedPromotionLevelsAdminContextTypeItem::class),
            )
        )
    )

    override fun run(node: JsonNode, paths: List<String>) {
        val config = node.parse<PredefinedPromotionLevelsAdminContextType>()
        syncForward(
            from = config.list,
            to = predefinedPromotionLevelService.predefinedPromotionLevels
        ) {
            equality { a, b -> a.name == b.name }
            onDeletion { existing ->
                if (config.replace) {
                    logger.info("Deleting predefined promotion level ${existing.name}")
                    predefinedPromotionLevelService.deletePredefinedPromotionLevel(existing.id)
                }
            }
            onCreation { item ->
                logger.info("Creating predefined promotion level ${item.name}")
                val ppl = predefinedPromotionLevelService.newPredefinedPromotionLevel(
                    PredefinedPromotionLevel.of(
                        NameDescription.nd(item.name, item.description)
                    )
                )
                if (item.image != null) {
                    logger.info("Setting the image for predefined promotion level ${item.name}")
                    setImage(ppl.id, item.image)
                }
            }
            onModification { item, existing ->
                logger.info("Updating predefined promotion level ${item.name}")
                predefinedPromotionLevelService.savePredefinedPromotionLevel(
                    PredefinedPromotionLevel.of(
                        NameDescription.nd(item.name, item.description)
                    ).withId(existing.id)
                )
                if (item.image != null) {
                    logger.info("Setting the image for predefined promotion level ${item.name}")
                    setImage(existing.id, item.image)
                }
            }
        }
    }

    private fun setImage(id: ID, uri: String) {
        val image = scmRefService.downloadDocument(uri, "image/png")
        if (image != null) {
            predefinedPromotionLevelService.setPredefinedPromotionLevelImage(id, image)
        }
    }

    override fun render(): JsonNode =
        predefinedPromotionLevelService.predefinedPromotionLevels.map {
            PredefinedPromotionLevelsAdminContextTypeItem(
                name = it.name,
                description = it.description ?: "",
                image = null,
            )
        }.let {
            PredefinedPromotionLevelsAdminContextType(
                replace = false,
                list = it,
            )
        }.asJson()
}

data class PredefinedPromotionLevelsAdminContextType(
    val replace: Boolean,
    val list: List<PredefinedPromotionLevelsAdminContextTypeItem>,
)

data class PredefinedPromotionLevelsAdminContextTypeItem(
    val name: String,
    val description: String,
    val image: String?,
)
