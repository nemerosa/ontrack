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
) : AbstractValidationDataType<Any?, String>(
        extensionFeature
) {
    override fun configFromJson(node: JsonNode?) {}

    override fun configToJson(config: Any?): JsonNode = NullNode.instance

    override fun getConfigForm(config: Any?): Form = Form.create()

    override fun configToFormJson(config: Any?) = null

    override fun fromConfigForm(node: JsonNode?) {}

    override fun toJson(data: String): JsonNode = JsonUtils.format(data)

    override fun fromJson(node: JsonNode): String? = node.textValue()

    override fun getForm(data: String?): Form = Form.create()
            .with(Text
                    .of("value")
                    .label("Text")
                    .value(data)
            )

    override fun fromForm(node: JsonNode?): String? =
            JsonUtils.get(node, "value")

    override fun computeStatus(config: Any?, data: String): ValidationRunStatusID? = null

    override fun validateData(config: Any?, data: String?): String {
        return validateNotNull(data)
    }

    override val displayName = "Free text"
}