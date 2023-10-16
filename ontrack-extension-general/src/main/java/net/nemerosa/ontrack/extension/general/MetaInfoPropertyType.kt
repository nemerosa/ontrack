package net.nemerosa.ontrack.extension.general

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.MultiForm
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertySearchArguments
import net.nemerosa.ontrack.model.structure.SearchIndexService
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function

@Component
class MetaInfoPropertyType(
    extensionFeature: GeneralExtensionFeature,
    private val searchIndexService: SearchIndexService,
    private val metaInfoSearchExtension: MetaInfoSearchExtension
) : AbstractPropertyType<MetaInfoProperty>(extensionFeature) {

    override fun getName(): String = "Meta information"

    override fun getDescription(): String = "List of meta information properties"

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> = EnumSet.allOf(ProjectEntityType::class.java)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean {
        return securityService.isProjectFunctionGranted(entity, ProjectConfig::class.java)
    }

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun onPropertyChanged(entity: ProjectEntity, value: MetaInfoProperty) {
        searchIndexService.createSearchIndex(metaInfoSearchExtension, MetaInfoSearchItem(entity, value))
    }

    override fun onPropertyDeleted(entity: ProjectEntity, oldValue: MetaInfoProperty) {
        searchIndexService.deleteSearchIndex(metaInfoSearchExtension, MetaInfoSearchItem(entity, oldValue).id)
    }

    override fun getEditionForm(entity: ProjectEntity, value: MetaInfoProperty?): Form = Form.create()
        .with(
            MultiForm.of(
                "items",
                Form.create()
                    .name()
                    .with(
                        Text.of("value").label("Value")
                    )
                    .with(
                        Text.of("link").label("Link").optional()
                    )
                    .with(
                        Text.of("category").label("Category").optional()
                    )
            )
                .label("Items")
                .value(value?.items ?: emptyList<Any>())
        )

    override fun fromClient(node: JsonNode): MetaInfoProperty {
        return fromStorage(node)
    }

    override fun fromStorage(node: JsonNode): MetaInfoProperty {
        return parse(node, MetaInfoProperty::class.java)
    }

    override fun containsValue(property: MetaInfoProperty, propertyValue: String): Boolean {
        val pos = StringUtils.indexOf(propertyValue, ":")
        return if (pos > 0) {
            val value = StringUtils.substringAfter(propertyValue, ":")
            val name = StringUtils.substringBefore(propertyValue, ":")
            property.matchNameValue(name, value)
        } else {
            false
        }
    }

    override fun replaceValue(
        value: MetaInfoProperty,
        replacementFunction: Function<String, String>
    ): MetaInfoProperty {
        return MetaInfoProperty(
            value.items
                .map { item ->
                    MetaInfoPropertyItem(
                        item.name,
                        item.value?.apply { replacementFunction.apply(this) },
                        item.link?.apply { replacementFunction.apply(this) },
                        item.category?.apply { replacementFunction.apply(this) }
                    )
                }
        )
    }

    override fun getSearchArguments(token: String): PropertySearchArguments? {
        val (name, value, _, category) = MetaInfoPropertyItem.parse(token)
        // If no name, no search is done
        if (name.isBlank()) {
            return null
        }
        // Criteria
        val criteria = mutableListOf<String>()
        val params = mutableMapOf<String, String>()
        // Name
        criteria += "item->>'name' = :name"
        params["name"] = name
        // Value
        if (value != null) {
            criteria += "item->>'value' ilike :value"
            params["value"] = value.toValuePattern()
        }
        // Category
        if (category != null) {
            if (category.isBlank()) {
                // Explicitly looking for names without categories
                criteria += "item->>'category' is null"
            } else {
                criteria += "item->>'category' = :category"
                params["category"] = category
            }
        }
        // OK
        return PropertySearchArguments(
            jsonContext = "jsonb_array_elements(pp.json->'items') as item",
            jsonCriteria = criteria.joinToString(" and "),
            criteriaParams = params,
        )
    }

    private fun String.toValuePattern(): String {
        return this.replace("*", "%")
    }
}
