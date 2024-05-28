package net.nemerosa.ontrack.model.docs

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import net.nemerosa.ontrack.model.annotations.getPropertyName
import java.time.LocalDateTime
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.*


fun getFieldsForDocumentationClass(documentationClass: KClass<*>): List<FieldDocumentation> {
    val fields = mutableListOf<FieldDocumentation>()
    documentationClass.declaredMemberProperties.forEach { property ->
        if (!property.hasAnnotation<DocumentationIgnore>()) {

            val name = getPropertyName(property)
            val description =
                property.findAnnotation<DocumentationType>()
                    ?.description?.takeIf { it.isNotBlank() }
                    ?: getPropertyDescription(property)

            // Type
            val fieldType = getFieldType(property)

            // Required?
            val required = !property.returnType.isMarkedNullable

            // Subfields
            val propertyType = property.returnType
            val subfields = if (propertyType.classifier == List::class && property.hasAnnotation<DocumentationList>()) {
                val itemType = property.returnType.arguments.firstOrNull()?.type?.classifier
                if (itemType != null && itemType is KClass<*>) {
                    getFieldsDocumentation(itemType)
                } else {
                    emptyList()
                }
            } else if (property.hasAnnotation<DocumentationField>()) {
                val actualPropertyType = propertyType.classifier
                if (actualPropertyType != null && actualPropertyType is KClass<*>) {
                    getFieldsDocumentation(actualPropertyType)
                } else {
                    emptyList()
                }
            } else {
                emptyList()
            }

            fields += FieldDocumentation(name, description, fieldType, required, subfields)
        }
    }
    // Super class
    if (documentationClass.hasAnnotation<DocumentationUseSuper>()) {
        val superClass = documentationClass.supertypes.firstOrNull()?.classifier
        if (superClass != null && superClass is KClass<*>) {
            fields += getFieldsForDocumentationClass(superClass)
        }
    }
    // OK
    return fields
}

fun getFieldsDocumentation(type: KClass<*>, section: String = ""): List<FieldDocumentation> {
    val documentationAnnotation = type.findAnnotations<Documentation>().firstOrNull { it.section == section }
    val documentationClass = documentationAnnotation?.value
        ?: if (type.hasAnnotation<SelfDocumented>()) {
            type
        } else {
            return emptyList()
        }
    return getFieldsForDocumentationClass(documentationClass)
}

fun getFieldType(property: KProperty1<out Any, *>): String {
    // Explicit annotation
    val typeAnnotation = property.findAnnotation<DocumentationType>()
    if (typeAnnotation != null) {
        return typeAnnotation.value
    }
    val classifier = property.returnType.classifier
    return when (classifier) {
        String::class -> "String"
        Int::class -> "Int"
        Long::class -> "Long"
        Boolean::class -> "Boolean"
        List::class -> "List"
        JsonNode::class -> "JSON"
        LocalDateTime::class -> "LocalDateTime"
        else -> if (classifier is KClass<*>) {
            when {
                classifier.isSubclassOf(Enum::class) ->
                    (classifier.java.enumConstants ?: error("Not an enum")).joinToString(", ")

                property.hasAnnotation<DocumentationField>() -> "Object"
                else -> error("Classifier $classifier for property $property is not supported")
            }
        } else {
            error("Classifier $classifier for property $property is not supported")
        }
    }
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

data class FieldDocumentation(
    val name: String,
    val description: String?,
    val type: String,
    val required: Boolean,
    val subfields: List<FieldDocumentation>,
)
