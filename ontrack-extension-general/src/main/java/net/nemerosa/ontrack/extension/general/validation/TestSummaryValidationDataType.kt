package net.nemerosa.ontrack.extension.general.validation

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.toJson
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.YesNo
import net.nemerosa.ontrack.model.structure.AbstractValidationDataType
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.springframework.stereotype.Component

/**
 * Validation data type associated to a CI test.
 */
@Component
class TestSummaryValidationDataType(
        extensionFeature: GeneralExtensionFeature
) : AbstractValidationDataType<TestSummaryValidationConfig, TestSummaryValidationData>(
        extensionFeature
) {

    override val displayName: String = "Test summary"

    override fun toJson(data: TestSummaryValidationData): JsonNode = data.toJson()!!

    override fun fromJson(node: JsonNode): TestSummaryValidationData? = node.parse()

    override fun getForm(data: TestSummaryValidationData?): Form = Form.create()
            .with(net.nemerosa.ontrack.model.form.Int
                    .of("passed")
                    .label("Passed")
                    .help("Count of passed tests")
                    .value(data?.passed)
                    .min(0)
            )
            .with(net.nemerosa.ontrack.model.form.Int
                    .of("skipped")
                    .label("Skipped")
                    .help("Count of skipped tests")
                    .value(data?.skipped)
                    .min(0)
            )
            .with(net.nemerosa.ontrack.model.form.Int
                    .of("failed")
                    .label("Failed")
                    .help("Count of failed tests")
                    .value(data?.failed)
                    .min(0)
            )

    override fun fromForm(node: JsonNode?): TestSummaryValidationData? =
            node?.parse()

    override fun validateData(config: TestSummaryValidationConfig?, data: TestSummaryValidationData?): TestSummaryValidationData =
            validateNotNull(data) {
                validate(passed >= 0, "Count of passed tests must be >= 0")
                validate(skipped >= 0, "Count of skipped tests must be >= 0")
                validate(failed >= 0, "Count of failed tests must be >= 0")
            }

    // Configuration aspects

    override fun configToJson(config: TestSummaryValidationConfig): JsonNode =
            config.toJson()!!

    override fun configFromJson(node: JsonNode?): TestSummaryValidationConfig? =
            node?.parse()

    override fun getConfigForm(config: TestSummaryValidationConfig?): Form = Form.create()
            .with(
                    YesNo.of("warningIfSkipped")
                            .label("Warning if skipped")
                            .help("If set to Yes, the status is set to warning if there is at least one skipped test.")
            )

    override fun configToFormJson(config: TestSummaryValidationConfig?): JsonNode? =
            config?.toJson()

    override fun fromConfigForm(node: JsonNode?): TestSummaryValidationConfig? =
            node?.parse()

    override fun computeStatus(config: TestSummaryValidationConfig?, data: TestSummaryValidationData): ValidationRunStatusID? {
        return config?.computeStatus(data)


    }

    override fun getMetrics(data: TestSummaryValidationData): Map<String, *>? {
        return mapOf(
                "passed" to data.passed,
                "skipped" to data.skipped,
                "failed" to data.failed,
                "total" to data.total
        )
    }

}