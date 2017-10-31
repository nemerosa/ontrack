package net.nemerosa.ontrack.extension.general.validation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.structure.AbstractValidationDataType
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.springframework.stereotype.Component

@Component
class TextValidationDataType(
        extensionFeature: GeneralExtensionFeature
) : AbstractValidationDataType<Unit, String>(
        extensionFeature
) {
    override fun configFromJson(node: JsonNode?) {}

    override fun configToJson(config: Unit): JsonNode = NullNode.instance

    override fun getConfigForm(config: Unit?): Form = Form.create()

    override fun fromConfigForm(node: JsonNode) {}

    override fun toJson(data: String): JsonNode = JsonUtils.format(data)

    override fun fromJson(node: JsonNode): String? = node.textValue()

    override fun getForm(data: String?): Form = Form.create()
            .with(Text
                    .of("value")
                    .label("Text")
                    .value(data)
            )

    override fun fromForm(node: JsonNode): String? =
            JsonUtils.get(node, "value")

    override fun computeStatus(config: Unit?, data: String): ValidationRunStatusID? = null

    override fun validateData(config: Unit?, data: String) {
    }

    override val displayName = "Free text"
}