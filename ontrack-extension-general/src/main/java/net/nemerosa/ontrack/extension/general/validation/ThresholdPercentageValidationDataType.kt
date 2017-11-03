package net.nemerosa.ontrack.extension.general.validation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.IntNode
import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.toJson
import net.nemerosa.ontrack.model.exceptions.ValidationRunDataInputException
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.structure.AbstractValidationDataType
import org.springframework.stereotype.Component

@Component
class ThresholdPercentageValidationDataType(
        extensionFeature: GeneralExtensionFeature
) : AbstractValidationDataType<ThresholdConfig, Int>(
        extensionFeature
) {
    override fun configFromJson(node: JsonNode?): ThresholdConfig? = node?.parse()

    override fun configToJson(config: ThresholdConfig): JsonNode = config.toJson()!!

    override fun getConfigForm(config: ThresholdConfig?): Form = config.toForm()

    override fun configToFormJson(config: ThresholdConfig?) = config?.toJson()

    override fun fromConfigForm(node: JsonNode?): ThresholdConfig? = node?.parse()

    override fun toJson(data: Int): JsonNode =
            IntNode(data)

    override fun fromJson(node: JsonNode): Int? {
        if (node is IntNode) {
            return node.asInt()
        } else {
            throw ValidationRunDataInputException(
                    "Data is expected to be an integer."
            )
        }
    }

    override fun getForm(data: Int?): Form = Form.create()
            .with(net.nemerosa.ontrack.model.form.Int
                    .of("value")
                    .label("Value (%)")
                    .value(data)
                    .min(0).max(100)
                    .optional()
            )

    override fun fromForm(node: JsonNode?): Int? {
        if (node != null && node.has("value")) {
            return node.get("value").asInt()
        } else {
            return null
        }
    }

    override fun computeStatus(config: ThresholdConfig?, data: Int) =
            config?.computeStatus(data)

    override fun validateData(config: ThresholdConfig?, data: Int?) =
            validateNotNull(data) {
                validate(this >= 0, "Percentage must be >= 0")
            }

    override val displayName = "Percentage"
}