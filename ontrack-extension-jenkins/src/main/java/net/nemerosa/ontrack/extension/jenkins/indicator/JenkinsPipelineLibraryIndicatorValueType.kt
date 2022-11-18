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

/**
 * The settings may define the following configuration for a given library:
 *
 * * is the version required?
 * * what is the latest supported version?
 * * what is the last deprecated version?
 * * what is the last unsupported version?
 *
 * Depending on the actual version being used and the `required` flag, the
 * status compliance will be computed as follows:
 *
 * * if the version is not set:
 *    * if the version is required --> 0% compliance
 *    * if the version is not required --> 100% compliance
 * * if the version is set:
 *    * version >= latest supported version (good) --> 100% compliance
 *    * version > latest deprecated version (not latest, but not deprecated) --> 66% compliance
 *    * version > latest unsupported version (deprecated, but still working) --> 33% compliance
 *    * version <= latest unsupported version (cannot work) --> 0% compliance
 */
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