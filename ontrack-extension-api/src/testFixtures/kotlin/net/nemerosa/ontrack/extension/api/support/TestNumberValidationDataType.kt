package net.nemerosa.ontrack.extension.api.support

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.json.toJson
import net.nemerosa.ontrack.model.exceptions.ValidationRunDataFormatException
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.structure.AbstractValidationDataType
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.springframework.stereotype.Component

@Component
class TestNumberValidationDataType(
        extensionFeature: TestExtensionFeature
) : AbstractValidationDataType<Int?, Int>(
        extensionFeature
) {
    override fun configFromJson(node: JsonNode?): Int? =
            when (node) {
                is IntNode -> node.asInt()
                else -> null
            }

    override fun configToJson(config: Int?): JsonNode =
            config?.let { IntNode(it) } ?: NullNode.instance

    override fun getConfigForm(config: Int?): Form = Form.create()
            .with(net.nemerosa.ontrack.model.form.Int
                    .of("threshold")
                    .label("Threshold")
                    .value(config)
                    .optional()
            )

    override fun configToFormJson(config: Int?): JsonNode? {
        return config?.let { mapOf("threshold" to it).toJson() }
    }

    override fun fromConfigForm(node: JsonNode?): Int? {
        if (node != null && node.has("threshold")) {
            return node.get("threshold").asInt()
        } else {
            return null
        }
    }

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

    override fun fromForm(node: JsonNode?): Int? =
            node?.run {
                if (has("value")) {
                    val valueNode = get("value")
                    if (valueNode.isInt) {
                        valueNode.asInt()
                    } else {
                        throw ValidationRunDataFormatException("`value` attribute for the run data must be an integer.")
                    }
                } else {
                    throw ValidationRunDataFormatException("Cannot find `value` attribute for the run data.")
                }
            }

    override fun computeStatus(config: Int?, data: Int): ValidationRunStatusID? {
        if (config != null) {
            return if (data > config) {
                ValidationRunStatusID.STATUS_PASSED
            } else {
                ValidationRunStatusID.STATUS_FAILED
            }
        } else {
            return null
        }
    }

    override fun validateData(config: Int?, data: Int?) =
            validateNotNull(data) {
                validate(this >= 0, "Value must be >= 0")
            }

    override val displayName = "Number with threshold"

    override fun getMetrics(data: Int): Map<String, *>? {
        return mapOf("value" to data)
    }
}