package net.nemerosa.ontrack.docs

import net.nemerosa.ontrack.model.annotations.getOptionalPropertyDescription
import net.nemerosa.ontrack.model.docs.DocumentationIgnore
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

fun isScalarClass(type: KClass<*>): Boolean = type in SCALAR_CLASSES

private val SCALAR_CLASSES: Set<KClass<*>> = setOf(
    Boolean::class,
    Byte::class,
    Short::class,
    Int::class,
    Long::class,
    Float::class,
    Double::class,
    Char::class,
    String::class,
)

/**
 * List all deep fields in a Kotlin object, associating each field with:
 *
 * * its property
 * * its path from the root of the `object`
 * * its value
 */
inline fun <reified T : Annotation> getObjectFields(root: Any): List<ObjectField<T>> {
    return getObjectFields(root, T::class)
}

/**
 * List all deep fields in a Kotlin object, associating each field with:
 *
 * * its property
 * * its path from the root of the `object`
 * * its value
 */
fun <T : Annotation> getObjectFields(root: Any, annotationClass: KClass<T>): List<ObjectField<T>> {

    fun createField(
        property: KProperty1<out Any, *>,
        path: List<String>,
    ): ObjectField<T> {
        val value = property.call()
        return ObjectField(
            name = property.name,
            description = getOptionalPropertyDescription(property),
            annotation = property.findAnnotations(annotationClass).firstOrNull(),
            path = path,
            value = value,
        )
    }

    fun collectObjectFields(
        current: Any,
        fields: MutableList<ObjectField<T>>,
        path: List<String>,
    ) {
        val klass = current::class
        klass.memberProperties.forEach { property: KProperty1<out Any, *> ->
            if (!property.hasAnnotation<DocumentationIgnore>() && property.visibility == KVisibility.PUBLIC) {
                val propertyType = property.returnType.classifier as KClass<*>
                if (isScalarClass(propertyType)) {
                    fields += createField(property, path)
                }
            }
        }
        klass.nestedClasses.forEach { nestedClass ->
            if (!nestedClass.hasAnnotation<DocumentationIgnore>()) {
                collectObjectFields(nestedClass.objectInstance!!, fields, path + nestedClass.simpleName!!)
            }
        }
    }

    val fields = mutableListOf<ObjectField<T>>()
    collectObjectFields(
        fields = fields,
        current = root,
        path = emptyList(),
    )

    return fields.sortedBy { it.name }
}

/**
 * Object field
 *
 * @see [getObjectFields]
 */
data class ObjectField<T : Annotation>(
    val name: String,
    val description: String?,
    val annotation: T?,
    val path: List<String>,
    val value: Any?,
)
