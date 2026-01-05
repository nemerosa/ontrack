package net.nemerosa.ontrack.extension.av.ci

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.mergeList
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfig
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.extension.config.extensions.CIConfigExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.merge
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.json.schema.JsonType
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.json.schema.toType
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class AutoVersioningBranchCIConfigExtension(
    autoVersioningExtensionFeature: AutoVersioningExtensionFeature,
    private val autoVersioningConfigurationService: AutoVersioningConfigurationService,
) : AbstractExtension(autoVersioningExtensionFeature), CIConfigExtension<AutoVersioningConfig> {

    override val id: String = "autoVersioning"

    override fun createJsonType(jsonTypeBuilder: JsonTypeBuilder): JsonType =
        jsonTypeBuilder.toType(AutoVersioningConfig::class)

    override fun parseData(data: JsonNode): AutoVersioningConfig = data.parse()

    override fun mergeConfig(
        defaults: AutoVersioningConfig,
        custom: JsonNode
    ): AutoVersioningConfig = mergeList<JsonNode>(
        target = defaults.configurations.map { it.asJson() },
        changes = custom.path("configurations").map { it },
        idFn = { cfg -> cfg.parse<AVCfgID>() }
    ) { e, existing ->
        existing.merge(e)
    }.map { e ->
        e.parse<AutoVersioningSourceConfig>()
    }.let {
        AutoVersioningConfig(it)
    }

    override val projectEntityTypes: Set<ProjectEntityType> = setOf(ProjectEntityType.BRANCH)

    override fun configure(
        entity: ProjectEntity,
        data: AutoVersioningConfig
    ) {
        autoVersioningConfigurationService.setupAutoVersioning(entity as Branch, data)
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class AVCfgID(
        val sourceProject: String,
        val sourceBranch: String,
        val qualifier: String? = null,
    )
}