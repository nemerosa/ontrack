package net.nemerosa.ontrack.extension.api.support

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.structure.AbstractValidationDataType
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.springframework.stereotype.Component


/**
 * Configuration data - none
 */

/**
 * Data
 */
data class TestValidationData(
        val critical: Int = 0,
        val high: Int = 0,
        val medium: Int = 0
)

/**
 * Validation type
 */
@Component
class TestValidationDataType(
        extensionFeature: TestExtensionFeature
) : AbstractValidationDataType<Any, TestValidationData>(
        extensionFeature
) {

    override fun validateData(config: Any?, data: TestValidationData?) =
            validateNotNull(data) {
                validate(critical >= 0, "Number of critical issues must be >= 0")
                validate(high >= 0, "Number of high issues must be >= 0")
                validate(medium >= 0, "Number of medium issues must be >= 0")
            }

    override fun configToJson(config: Any): JsonNode = NullNode.instance

    override fun configFromJson(node: JsonNode?): Any? = null

    override fun getConfigForm(config: Any?): Form = Form.create()

    override fun configToFormJson(config: Any?) = null

    override fun fromConfigForm(node: JsonNode?) {}

    override fun toJson(data: TestValidationData): JsonNode = JsonUtils.format(data)

    override fun fromJson(node: JsonNode): TestValidationData? = JsonUtils.parse(node, TestValidationData::class.java)

    override fun getForm(data: TestValidationData?): Form =
            Form.create()
                    .with(
                            net.nemerosa.ontrack.model.form.Int
                                    .of("critical")
                                    .label("Critical issues")
                                    .min(0)
                                    .value(data?.critical)
                    )
                    .with(
                            net.nemerosa.ontrack.model.form.Int
                                    .of("high")
                                    .label("High issues")
                                    .min(0)
                                    .value(data?.high)
                    )
                    .with(
                            net.nemerosa.ontrack.model.form.Int
                                    .of("medium")
                                    .label("Medium issues")
                                    .min(0)
                                    .value(data?.medium)
                    )

    override fun fromForm(node: JsonNode?): TestValidationData? =
            node?.run {
                JsonUtils.parse(this, TestValidationData::class.java)
            }

    override fun computeStatus(config: Any?, data: TestValidationData): ValidationRunStatusID? =
            when {
                data.critical > 0 -> ValidationRunStatusID.STATUS_FAILED
                data.high > 0 -> ValidationRunStatusID.STATUS_WARNING
                else -> ValidationRunStatusID.STATUS_PASSED
            }

    override val displayName = "Test validation data"

    override fun getMetrics(data: TestValidationData): Map<String, *>? {
        return mapOf(
                "critical" to data.critical,
                "high" to data.high,
                "medium" to data.medium
        )
    }
}