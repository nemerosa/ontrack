package net.nemerosa.ontrack.extension.environments.ci

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.mergeList
import net.nemerosa.ontrack.extension.config.extensions.CIConfigExtension
import net.nemerosa.ontrack.extension.environments.EnvironmentsExtensionFeature
import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.casc.EnvironmentCasc
import net.nemerosa.ontrack.extension.environments.casc.EnvironmentsInjection
import net.nemerosa.ontrack.extension.environments.casc.SlotCascDef
import net.nemerosa.ontrack.extension.environments.casc.SlotEnvironmentCasc
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.json.schema.JsonType
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.json.schema.toType
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class EnvironmentsCIConfigExtension(
    feature: EnvironmentsExtensionFeature,
    private val environmentsInjection: EnvironmentsInjection,
) : AbstractExtension(feature),
    CIConfigExtension<EnvironmentsCIConfigExtension.EnvironmentsCIConfigExtensionConfig> {

    override val id: String = "environments"

    override val projectEntityTypes: Set<ProjectEntityType> = setOf(ProjectEntityType.PROJECT)

    override fun parseData(data: JsonNode) = data.parse<EnvironmentsCIConfigExtensionConfig>()

    override fun mergeData(
        defaults: EnvironmentsCIConfigExtensionConfig,
        custom: EnvironmentsCIConfigExtensionConfig
    ): EnvironmentsCIConfigExtensionConfig =
        EnvironmentsCIConfigExtensionConfig(
            environments = mergeList(
                target = defaults.environments,
                changes = custom.environments,
                idFn = EnvironmentCasc::name,
            ) { e, existing ->
                existing.copy(
                    tags = mergeList(
                        target = existing.tags,
                        changes = e.tags,
                        idFn = { it }
                    ) { t, _ -> t },
                )
            },
            slots = mergeList(
                target = defaults.slots,
                changes = custom.slots,
                idFn = SlotCasc::qualifier,
            ) { slotChanges, slotExisting ->
                slotExisting.copy(
                    environments = mergeList(
                        target = slotExisting.environments,
                        changes = slotChanges.environments,
                        idFn = SlotEnvironmentCasc::name
                    ) { e, _ -> e }
                )
            }
        )

    override fun createJsonType(jsonTypeBuilder: JsonTypeBuilder): JsonType =
        jsonTypeBuilder.toType(EnvironmentsCIConfigExtensionConfig::class)

    override fun configure(
        entity: ProjectEntity,
        data: EnvironmentsCIConfigExtensionConfig
    ) {
        val project = entity as Project

        // Injection of environments
        environmentsInjection.defineEnvironments(
            environments = data.environments,
            keepEnvironments = true,
        )

        // Injection of slots for this project
        environmentsInjection.defineSlots(
            slots = data.slots,
        ) { project }
    }

    data class EnvironmentsCIConfigExtensionConfig(
        @APIDescription("List of environments")
        val environments: List<EnvironmentCasc>,
        @APIDescription("List of deployments slots (associated of a project and an environment)")
        val slots: List<SlotCasc> = emptyList(),
    )

    @APIDescription("Definition for a slot: the association of an optional qualifier to several environments")
    data class SlotCasc(
        @APIDescription("Optional qualifier for this slot")
        override val qualifier: String = Slot.DEFAULT_QUALIFIER,
        @APIDescription("Prefix for the description for the slots (can be overridden by description at environment level)")
        override val description: String = "",
        @APIDescription("Configuration of environments for this slot")
        override val environments: List<SlotEnvironmentCasc>,
    ) : SlotCascDef

}