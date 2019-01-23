package net.nemerosa.ontrack.extension.general

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.NamedEntries
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertySearchArguments
import net.nemerosa.ontrack.model.support.NameValue
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function

@Component
class LinkPropertyType(
        extensionFeature: GeneralExtensionFeature
) : AbstractPropertyType<LinkProperty>(extensionFeature) {

    override fun getName(): String = "Links"

    override fun getDescription(): String = "List of links."

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> =
            EnumSet.allOf(ProjectEntityType::class.java)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean =
            securityService.isProjectFunctionGranted(entity, ProjectConfig::class.java)

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun getEditionForm(entity: ProjectEntity, value: LinkProperty?): Form = Form.create()
            .with(
                    NamedEntries.of("links")
                            .label("List of links")
                            .nameLabel("Name")
                            .valueLabel("Link")
                            .nameOptional()
                            .addText("Add a link")
                            .help("List of links associated with a name.")
                            .value(value?.links ?: emptyList<Any>())
            )

    override fun fromClient(node: JsonNode): LinkProperty {
        return fromStorage(node)
    }

    override fun fromStorage(node: JsonNode): LinkProperty {
        return AbstractPropertyType.parse(node, LinkProperty::class.java)
    }

    override fun getSearchKey(value: LinkProperty): String {
        return ""
    }

    override fun replaceValue(value: LinkProperty, replacementFunction: Function<String, String>): LinkProperty {
        return LinkProperty(
                value.links.map { nv ->
                    NameValue(nv.name, replacementFunction.apply(nv.value))
                }
        )
    }

    override fun getSearchArguments(token: String): PropertySearchArguments? {
        return PropertySearchArguments(
                jsonContext = "jsonb_array_elements(pp.json->'links') as link",
                jsonCriteria = "link->>'value' ilike :value",
                criteriaParams = mapOf(
                        "value" to "%$token%"
                )
        )
    }

    override fun containsValue(value: LinkProperty, propertyValue: String): Boolean =
            value.links.map {
                it.value
            }.any {
                StringUtils.containsIgnoreCase(it, propertyValue)
            }
}
