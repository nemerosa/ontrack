package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.templating.AbstractTemplatingSource
import net.nemerosa.ontrack.model.templating.getBooleanTemplatingParam
import net.nemerosa.ontrack.model.templating.getRequiredTemplatingParam
import org.springframework.stereotype.Component

@Component
@APIDescription("Gets some meta information from a project entity.")
@Documentation(MetaInfoPropertyTemplatingSourceConfig::class)
@DocumentationExampleCode("${'$'}{build.release}")
class MetaInfoPropertyTemplatingSource(
    private val propertyService: PropertyService,
) : AbstractTemplatingSource(
    field = "meta",
    types = ProjectEntityType.values().toSet(),
) {
    override fun render(entity: ProjectEntity, configMap: Map<String, String>, renderer: EventRenderer): String {
        val name = configMap.getRequiredTemplatingParam(MetaInfoPropertyTemplatingSourceConfig::name.name)
        val category = configMap[MetaInfoPropertyTemplatingSourceConfig::category.name]
        val error = configMap.getBooleanTemplatingParam(MetaInfoPropertyTemplatingSourceConfig::error.name)
        val link = configMap.getBooleanTemplatingParam(MetaInfoPropertyTemplatingSourceConfig::link.name)

        val property = propertyService.getPropertyValue(entity, MetaInfoPropertyType::class.java)
        val item = property?.findMetaInfoItems(
            name = name,
            category = category,
        )?.firstOrNull()

        return if (item != null) {
            if (link && !item.link.isNullOrBlank() && !item.value.isNullOrBlank()) {
                renderer.renderLink(item.value, item.link)
            } else {
                item.value ?: ""
            }
        } else if (error) {
            throw MetaInfoPropertyTemplatingSourceMissingKeyException(name)
        } else {
            ""
        }
    }
}