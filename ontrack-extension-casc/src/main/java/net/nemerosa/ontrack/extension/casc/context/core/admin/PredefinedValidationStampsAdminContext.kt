package net.nemerosa.ontrack.extension.casc.context.core.admin

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.syncForward
import net.nemerosa.ontrack.extension.casc.context.AbstractCascContext
import net.nemerosa.ontrack.extension.casc.schema.CascType
import net.nemerosa.ontrack.extension.casc.schema.cascArray
import net.nemerosa.ontrack.extension.casc.schema.cascField
import net.nemerosa.ontrack.extension.casc.schema.cascObject
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.files.FileRefService
import net.nemerosa.ontrack.model.files.downloadDocument
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PredefinedValidationStampsAdminContext(
    private val predefinedValidationStampService: PredefinedValidationStampService,
    private val scmRefService: FileRefService,
) : AbstractCascContext(), SubAdminContext {

    private val logger: Logger = LoggerFactory.getLogger(PredefinedValidationStampsAdminContext::class.java)

    override val field: String = "predefined-validation-stamps"

    override val type: CascType = cascObject(
        description = "Predefined validation stamps",
        cascField(PredefinedValidationStampsAdminContextType::replace),
        cascField(
            name = "list",
            description = "List of validation stamps",
            required = true,
            type = cascArray(
                description = "List of validation stamps",
                type = cascObject(PredefinedValidationStampsAdminContextTypeItem::class),
            )
        )
    )

    override fun run(node: JsonNode, paths: List<String>) {
        val config = node.parse<PredefinedValidationStampsAdminContextType>()
        syncForward(
            from = config.list,
            to = predefinedValidationStampService.predefinedValidationStamps
        ) {
            equality { a, b -> a.name == b.name }
            onDeletion { existing ->
                if (config.replace) {
                    logger.info("Deleting predefined validation stamp ${existing.name}")
                    predefinedValidationStampService.deletePredefinedValidationStamp(existing.id)
                }
            }
            onCreation { item ->
                logger.info("Creating predefined validation stamp ${item.name}")
                val ppl = predefinedValidationStampService.newPredefinedValidationStamp(
                    PredefinedValidationStamp.of(
                        NameDescription.nd(item.name, item.description)
                    )
                )
                if (item.image != null) {
                    logger.info("Setting the image for predefined validation stamp ${item.name}")
                    setImage(ppl.id, item.image)
                }
            }
            onModification { item, existing ->
                logger.info("Updating predefined validation stamp ${item.name}")
                predefinedValidationStampService.savePredefinedValidationStamp(
                    PredefinedValidationStamp.of(
                        NameDescription.nd(item.name, item.description)
                    ).withId(existing.id)
                )
                if (item.image != null) {
                    logger.info("Setting the image for predefined validation stamp ${item.name}")
                    setImage(existing.id, item.image)
                }
            }
        }
    }

    private fun setImage(id: ID, uri: String) {
        val image = scmRefService.downloadDocument(uri, "image/png")
        if (image != null) {
            predefinedValidationStampService.setPredefinedValidationStampImage(id, image)
        }
    }

    override fun render(): JsonNode =
        predefinedValidationStampService.predefinedValidationStamps.map {
            PredefinedValidationStampsAdminContextTypeItem(
                name = it.name,
                description = it.description ?: "",
                image = null,
            )
        }.let {
            PredefinedValidationStampsAdminContextType(
                replace = false,
                list = it,
            )
        }.asJson()
}

data class PredefinedValidationStampsAdminContextType(
    val replace: Boolean,
    val list: List<PredefinedValidationStampsAdminContextTypeItem>,
)

data class PredefinedValidationStampsAdminContextTypeItem(
    val name: String,
    val description: String,
    val image: String?,
)
