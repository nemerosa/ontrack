package net.nemerosa.ontrack.extension.av.validation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Int
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.structure.AbstractValidationDataType
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.springframework.stereotype.Component

@Component
class AutoVersioningValidationDataType(
    extensionFeature: AutoVersioningExtensionFeature,
) : AbstractValidationDataType<Any?, AutoVersioningValidationData>(
    extensionFeature
) {
    override val displayName: String = "Auto versioning"

    override fun computeStatus(config: Any?, data: AutoVersioningValidationData): ValidationRunStatusID? =
        if (data.latestVersion.isBlank() || data.version.isBlank() || data.latestVersion != data.version) {
            ValidationRunStatusID.STATUS_FAILED
        } else {
            ValidationRunStatusID.STATUS_PASSED
        }

    override fun configFromJson(node: JsonNode?) {}

    override fun configToFormJson(config: Any?): JsonNode? = null

    override fun configToJson(config: Any?): JsonNode = NullNode.instance

    override fun fromConfigForm(node: JsonNode?) {}

    override fun fromForm(node: JsonNode?): AutoVersioningValidationData? = node?.parse()

    override fun fromJson(node: JsonNode): AutoVersioningValidationData? = AutoVersioningValidationData(
        project = node["project"].textValue(),
        version = node["version"].textValue(),
        latestVersion = node.path("latestVersion").asText(), // Might not be there in legacy storage
        path = node["path"].textValue(),
        time = node["time"].longValue()
    )

    override fun getConfigForm(config: Any?): Form = Form.create()

    override fun getForm(data: AutoVersioningValidationData?): Form = Form.create()
        .with(
            Text.of(AutoVersioningValidationData::project.name).label("Project").value(data?.project)
        )
        .with(
            Text.of(AutoVersioningValidationData::version.name).label("Version").value(data?.version)
        )
        .with(
            Text.of(AutoVersioningValidationData::latestVersion.name).label("Latest version").value(data?.latestVersion)
        )
        .with(
            Text.of(AutoVersioningValidationData::path.name).label("Path").value(data?.path)
        )
        .with(
            Int.of(AutoVersioningValidationData::time.name).label("Time (ms)").value(data?.time)
        )

    override fun getMetrics(data: AutoVersioningValidationData): Map<String, *>? = mapOf(
        "executionTime" to data.time
    )

    override fun toJson(data: AutoVersioningValidationData): JsonNode = data.asJson()

    override fun validateData(config: Any?, data: AutoVersioningValidationData?): AutoVersioningValidationData =
        validateNotNull(data)
}