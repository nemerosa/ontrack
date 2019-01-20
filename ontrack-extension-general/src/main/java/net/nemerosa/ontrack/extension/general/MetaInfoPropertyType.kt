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
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function

@Component
class MetaInfoPropertyType(
        extensionFeature: GeneralExtensionFeature
) : AbstractPropertyType<MetaInfoProperty>(extensionFeature) {

    override fun getName(): String = "Meta information"

    override fun getDescription(): String = "List of meta information properties"

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> = EnumSet.allOf(ProjectEntityType::class.java)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean {
        return securityService.isProjectFunctionGranted(entity, ProjectConfig::class.java)
    }

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

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
                            .value(if (value != null) value.items else emptyList<Any>())
            )

    override fun fromClient(node: JsonNode): MetaInfoProperty {
        return fromStorage(node)
    }

    override fun fromStorage(node: JsonNode): MetaInfoProperty {
        return AbstractPropertyType.parse(node, MetaInfoProperty::class.java)
    }

    override fun getSearchKey(value: MetaInfoProperty): String {
        return value.items
                .joinToString(separator = ";") {
                    "${it.name}:${it.value}"
                }
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

    override fun replaceValue(value: MetaInfoProperty, replacementFunction: Function<String, String>): MetaInfoProperty {
        return MetaInfoProperty(
                value.items
                        .map { item ->
                            MetaInfoPropertyItem(
                                    item.name,
                                    replacementFunction.apply(item.value),
                                    item.link?.apply { replacementFunction.apply(this) },
                                    item.category?.apply { replacementFunction.apply(this) }
                            )
                        }
        )
    }

    override fun getSearchArguments(token: String): PropertySearchArguments? {
        val name: String?
        val value: String?
        if (token.indexOf(":") >= 1) {
            name = token.substringBefore(":").trim()
            value = token.substringAfter(":").trimStart()
        } else {
            name = null
            value = token
        }
        return if (name.isNullOrBlank()) {
            if (value.isNullOrBlank()) {
                // Empty
                null
            } else {
                // Value only
                PropertySearchArguments(
                        jsonContext = "jsonb_array_elements(json->'items') as item",
                        jsonCriteria = "item->>'value' like :value",
                        criteriaParams = mapOf(
                                "value" to value.toValuePattern()
                        )
                )
            }
        } else if (value.isNullOrBlank()) {
            // Name only
            PropertySearchArguments(
                    jsonContext = "jsonb_array_elements(json->'items') as item",
                    jsonCriteria = "item->>'name' = :name",
                    criteriaParams = mapOf(
                            "name" to name
                    )
            )
        } else {
            // Name & value
            PropertySearchArguments(
                    jsonContext = "jsonb_array_elements(pp.json->'items') as item",
                    jsonCriteria = "item->>'name' = :name and item->>'value' like :value",
                    criteriaParams = mapOf(
                            "name" to name,
                            "value" to value.toValuePattern()
                    )
            )
        }
    }

    private fun String.toValuePattern(): String {
        return this.replace("*", "%")
    }
}
