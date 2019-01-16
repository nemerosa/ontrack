package net.nemerosa.ontrack.extension.general.validation

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.json.getInt
import net.nemerosa.ontrack.json.getRequiredEnum
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.toJson
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.selection
import net.nemerosa.ontrack.model.structure.AbstractValidationDataType
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.springframework.stereotype.Component
import net.nemerosa.ontrack.model.form.Int as IntField

/**
 * Validation data based on the critical / high / medium / low numbers.
 */
@Component
class CHMLValidationDataType(
        extensionFeature: GeneralExtensionFeature
) : AbstractValidationDataType<CHMLValidationDataTypeConfig, CHMLValidationDataTypeData>(
        extensionFeature
) {
    override val displayName = "Critical / high / medium / low"

    override fun configToJson(config: CHMLValidationDataTypeConfig) = config.toJson()!!

    override fun configFromJson(node: JsonNode?): CHMLValidationDataTypeConfig? = node?.parse()

    override fun getConfigForm(config: CHMLValidationDataTypeConfig?): Form =
            Form.create()
                    .with(
                            selection<CHML>("failedLevel", CHML::displayName)
                                    .label("Failed if")
                                    .value(config?.failedLevel?.level)
                    )
                    .with(
                            IntField.of("failedValue")
                                    .label("greater or equal than")
                                    .min(0)
                                    .value(config?.failedLevel?.value ?: 0)
                    )
                    .with(
                            selection<CHML>("warningLevel", CHML::displayName)
                                    .label("Warning if")
                                    .value(config?.warningLevel?.level)
                    )
                    .with(
                            IntField.of("warningValue")
                                    .label("greater or equal than")
                                    .min(0)
                                    .value(config?.warningLevel?.value ?: 0)
                    )

    override fun configToFormJson(config: CHMLValidationDataTypeConfig?) =
            config?.let {
                mapOf(
                        "failedLevel" to it.failedLevel.level,
                        "failedValue" to it.failedLevel.value,
                        "warningLevel" to it.warningLevel.level,
                        "warningValue" to it.warningLevel.value
                ).toJson()
            }

    override fun fromConfigForm(node: JsonNode?) =
            node?.let {
                CHMLValidationDataTypeConfig(
                        warningLevel = CHMLLevel(
                                node.getRequiredEnum("warningLevel"),
                                node.getInt("warningValue") ?: 0
                        ),
                        failedLevel = CHMLLevel(
                                node.getRequiredEnum("failedLevel"),
                                node.getInt("failedValue") ?: 0
                        )
                )
            }

    override fun toJson(data: CHMLValidationDataTypeData) = data.toJson()!!

    override fun fromJson(node: JsonNode): CHMLValidationDataTypeData = node.parse()

    override fun getForm(data: CHMLValidationDataTypeData?): Form =
            Form.create()
                    .with(
                            CHML.values().map {
                                IntField.of(it.name)
                                        .label(it.displayName)
                                        .optional()
                                        .min(0)
                                        .value(data?.levels?.get(it) ?: 0)
                            }
                    )

    override fun fromForm(node: JsonNode?): CHMLValidationDataTypeData? =
            node?.let {
                CHMLValidationDataTypeData(
                        CHML.values().associate {
                            it to (node.getInt(it.name) ?: 0)
                        }
                )
            }

    override fun computeStatus(config: CHMLValidationDataTypeConfig?, data: CHMLValidationDataTypeData): ValidationRunStatusID? {
        if (config != null) {
            if (config.failedLevel.value > 0) {
                if ((data.levels[config.failedLevel.level] ?: 0) >= config.failedLevel.value) {
                    return ValidationRunStatusID.STATUS_FAILED
                }
            }
            if (config.warningLevel.value > 0) {
                if ((data.levels[config.warningLevel.level] ?: 0) >= config.warningLevel.value) {
                    return ValidationRunStatusID.STATUS_WARNING
                }
            }
            return ValidationRunStatusID.STATUS_PASSED
        } else {
            return null
        }
    }

    override fun validateData(config: CHMLValidationDataTypeConfig?, data: CHMLValidationDataTypeData?) =
            validateNotNull(data) {
                CHML.values().forEach {
                    val value = levels[it]
                    if (value != null) {
                        validate(value >= 0, "Value for ${it} must be >= 0")
                    }
                }
            }

    override fun getMetrics(data: CHMLValidationDataTypeData): Map<String, *>? {
        return mapOf(
                "critical" to (data.levels[CHML.CRITICAL] ?: 0),
                "high" to (data.levels[CHML.HIGH] ?: 0),
                "medium" to (data.levels[CHML.MEDIUM] ?: 0),
                "low" to (data.levels[CHML.LOW] ?: 0)
        )
    }
}

data class CHMLValidationDataTypeData(
        val levels: Map<CHML, Int>
)

data class CHMLValidationDataTypeConfig(
        val warningLevel: CHMLLevel,
        val failedLevel: CHMLLevel
)

data class CHMLLevel(
        val level: CHML,
        val value: Int
) {
    init {
        if (value < 0) throw IllegalArgumentException("Value must be >= 0")
    }
}

enum class CHML(val displayName: String) {
    CRITICAL("Critical"),
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low")
}
