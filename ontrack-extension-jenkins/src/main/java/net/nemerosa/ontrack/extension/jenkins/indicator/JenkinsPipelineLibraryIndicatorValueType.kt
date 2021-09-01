package net.nemerosa.ontrack.extension.jenkins.indicator

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCompliance
import net.nemerosa.ontrack.extension.indicators.model.IndicatorValueType
import net.nemerosa.ontrack.extension.jenkins.JenkinsExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.form.YesNo
import org.springframework.stereotype.Component

@Component
class JenkinsPipelineLibraryIndicatorValueType(
    extension: JenkinsExtensionFeature
) : AbstractExtension(extension),
    IndicatorValueType<JenkinsPipelineLibraryVersion?, JenkinsPipelineLibraryIndicatorValueTypeConfig> {

    override val name: String = "Jenkins pipeline library"

    override fun form(
        config: JenkinsPipelineLibraryIndicatorValueTypeConfig,
        value: JenkinsPipelineLibraryVersion?
    ): Form = Form.create()
        .with(
            Text.of("version")
                .optional()
                .label("Version")
                .value(value?.value)
        )

    override fun status(
        config: JenkinsPipelineLibraryIndicatorValueTypeConfig,
        value: JenkinsPipelineLibraryVersion?
    ): IndicatorCompliance =
        when {
            config.versionRequired && value == null -> IndicatorCompliance.LOWEST
            config.versionMinimum != null && value != null && value < config.versionMinimum -> IndicatorCompliance.MEDIUM
            else -> IndicatorCompliance.HIGHEST
        }

    override fun toClientString(
        config: JenkinsPipelineLibraryIndicatorValueTypeConfig,
        value: JenkinsPipelineLibraryVersion?
    ): String = value?.value ?: ""

    override fun toClientJson(
        config: JenkinsPipelineLibraryIndicatorValueTypeConfig,
        value: JenkinsPipelineLibraryVersion?
    ): JsonNode = mapOf("version" to value?.value).asJson()

    override fun fromClientJson(
        config: JenkinsPipelineLibraryIndicatorValueTypeConfig,
        value: JsonNode
    ): JenkinsPipelineLibraryVersion? {
        val version = value.path("version").asText()
        return if (version.isNullOrBlank()) {
            null
        } else {
            JenkinsPipelineLibraryVersion(version)
        }
    }

    override fun fromStoredJson(
        valueConfig: JenkinsPipelineLibraryIndicatorValueTypeConfig,
        value: JsonNode
    ): JenkinsPipelineLibraryVersion? =
        value.takeIf { !it.isNull }?.asText()?.takeIf { it.isNotBlank() }?.run { JenkinsPipelineLibraryVersion(this) }

    override fun toStoredJson(
        config: JenkinsPipelineLibraryIndicatorValueTypeConfig,
        value: JenkinsPipelineLibraryVersion?
    ): JsonNode = value?.let { TextNode.valueOf(it.value) } ?: NullNode.instance

    override fun configForm(config: JenkinsPipelineLibraryIndicatorValueTypeConfig?): Form = Form.create()
        .with(
            YesNo.of(JenkinsPipelineLibraryIndicatorValueTypeConfig::versionRequired.name)
                .label("Version required")
                .value(config?.versionRequired ?: false)
        )
        .with(
            Text.of(JenkinsPipelineLibraryIndicatorValueTypeConfig::versionMinimum.name)
                .optional()
                .label("Minimum version")
                .value(config?.versionMinimum?.value)
        )

    override fun toConfigForm(config: JenkinsPipelineLibraryIndicatorValueTypeConfig): JsonNode =
        mapOf(
            JenkinsPipelineLibraryIndicatorValueTypeConfig::versionRequired.name to config.versionRequired,
            JenkinsPipelineLibraryIndicatorValueTypeConfig::versionMinimum.name to config.versionMinimum?.value
        ).asJson()

    override fun fromConfigForm(config: JsonNode): JenkinsPipelineLibraryIndicatorValueTypeConfig {
        val required = config.path(JenkinsPipelineLibraryIndicatorValueTypeConfig::versionRequired.name).asBoolean()
        val minimum = config.path(JenkinsPipelineLibraryIndicatorValueTypeConfig::versionMinimum.name).asText()
        return JenkinsPipelineLibraryIndicatorValueTypeConfig(
            versionRequired = required,
            versionMinimum = minimum?.takeIf { it.isNotBlank() }?.let { JenkinsPipelineLibraryVersion(it) }
        )
    }

    override fun toConfigClientJson(config: JenkinsPipelineLibraryIndicatorValueTypeConfig): JsonNode =
        config.asJson()

    override fun toConfigStoredJson(config: JenkinsPipelineLibraryIndicatorValueTypeConfig): JsonNode =
        config.asJson()

    override fun fromConfigStoredJson(config: JsonNode): JenkinsPipelineLibraryIndicatorValueTypeConfig =
        config.parse()
}