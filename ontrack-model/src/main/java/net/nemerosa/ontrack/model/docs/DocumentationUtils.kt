package net.nemerosa.ontrack.model.docs

import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import net.nemerosa.ontrack.model.annotations.getPropertyName
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

fun getFieldsDocumentation(type: KClass<*>): Map<String, String> {
    val documentation = type.findAnnotation<Documentation>()
        ?: return emptyMap()
    val fields = mutableMapOf<String, String>()
    documentation.value.declaredMemberProperties.forEach { property ->
        val name = getPropertyName(property)
        val description = getPropertyDescription(property)
        fields[name] = description
    }
    return fields
}

fun getDocumentationExampleCode(
    type: KClass<*>,
    trimIndent: Boolean = true,
): String? =
    type.findAnnotation<DocumentationExampleCode>()
        ?.value
        ?.run {
            if (trimIndent) {
                trimIndent().trim()
            } else {
                this
            }
        }
