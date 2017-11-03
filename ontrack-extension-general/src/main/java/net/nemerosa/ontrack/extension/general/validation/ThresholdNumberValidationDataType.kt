package net.nemerosa.ontrack.extension.general.validation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.IntNode
import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.toJson
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.structure.AbstractValidationDataType
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.springframework.stereotype.Component

@Component
class ThresholdNumberValidationDataType(
        extensionFeature: GeneralExtensionFeature
) : AbstractValidationDataType<ThresholdConfig, Int>(
        extensionFeature
) {
    override fun configFromJson(node: JsonNode?): ThresholdConfig? =
            node?.parse()

    override fun configToJson(config: ThresholdConfig) = config.toJson()!!

    override fun getConfigForm(config: ThresholdConfig?): Form = config.toForm()

    override fun configToFormJson(config: ThresholdConfig?): JsonNode? = config?.toJson()

    override fun fromConfigForm(node: JsonNode?): ThresholdConfig? = node?.parse()

    override fun toJson(data: Int): JsonNode = IntNode(data)

    override fun fromJson(node: JsonNode): Int? =
            when (node) {
                is IntNode -> node.asInt()
                else -> null
            }

    override fun getForm(data: Int?): Form = Form.create()
            .with(net.nemerosa.ontrack.model.form.Int
                    .of("value")
                    .label("Value")
                    .value(data)
                    .optional()
            )

    override fun fromForm(node: JsonNode?): Int? {
        if (node != null && node.has("value")) {
            return node.get("value").asInt()
        } else {
            return null
        }
    }

    override fun computeStatus(config: ThresholdConfig?, data: Int): ValidationRunStatusID? =
            config?.computeStatus(data)

    override fun validateData(config: ThresholdConfig?, data: Int?) =
            validateNotNull(data)

    override val displayName = "Numeric data"

}