package net.nemerosa.ontrack.graphql.support

import com.fasterxml.jackson.databind.JsonNode
import graphql.Scalars.*
import graphql.schema.*
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.model.annotations.APIDescription
import org.apache.commons.lang3.reflect.FieldUtils
import org.springframework.beans.BeanUtils
import java.beans.PropertyDescriptor
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.time.LocalDateTime
import java.util.*
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaType

object GraphQLBeanConverter {

    private val DEFAULT_EXCLUSIONS = setOf(
            "class"
    )

    fun asInputFields(type: KClass<*>): List<GraphQLInputObjectField> {
        val fields = mutableListOf<GraphQLInputObjectField>()
        // Gets the properties for the type
        type.memberProperties.forEach { property ->
            val name = getPropertyName(property)
            val description = getPropertyDescription(property)
            val scalarType = getScalarType(property.returnType)
            if (scalarType != null) {
                val actualType: GraphQLInputType = if (property.returnType.isMarkedNullable) {
                    scalarType
                } else {
                    GraphQLNonNull(scalarType)
                }
                fields += GraphQLInputObjectField.newInputObjectField()
                        .name(name)
                        .description(description)
                        .type(actualType)
                        .build()
            } else {
                val typeRef = property.findAnnotation<TypeRef>()
                if (typeRef != null) {
                    val javaType = property.returnType.javaType
                    if (javaType is Class<*>) {
                        val rootType = GraphQLTypeReference(javaType.simpleName)
                        val actualType: GraphQLInputType = if (property.returnType.isMarkedNullable) {
                            rootType
                        } else {
                            GraphQLNonNull(rootType)
                        }
                        fields += GraphQLInputObjectField.newInputObjectField()
                            .name(name)
                            .description(description)
                            .type(actualType)
                            .build()
                    } else {
                        throw IllegalStateException("Unsupported type for input type: $property")
                    }
                } else {
                    throw IllegalStateException("Cannot create an input field out of $property since its type is not scalar and the property is not annotated with @TypeRef")
                }
            }
        }
        // OK
        return fields
    }

    @Deprecated("Use method with KClass")
    fun asInputFields(type: Class<*>): List<GraphQLInputObjectField> {
        val fields = mutableListOf<GraphQLInputObjectField>()
        // Gets the properties for the type
        for (descriptor in BeanUtils.getPropertyDescriptors(type)) {
            if (descriptor.readMethod != null) {
                val name = descriptor.name
                val description = getDescription(type, descriptor)
                val scalarType = getScalarType(descriptor.propertyType)
                if (scalarType != null) {
                    fields += GraphQLInputObjectField.newInputObjectField()
                            .name(name)
                            .description(description)
                            .type(scalarType)
                            .build()
                }
            }
        }
        // OK
        return fields
    }

    @Deprecated("No replacement yet, but Java class must be avoided")
    fun asInputType(type: Class<*>): GraphQLInputType {
        return GraphQLInputObjectType.newInputObject()
                .name(type.simpleName)
                .fields(asInputFields(type))
                .build()
    }

    fun asObjectType(type: KClass<*>, cache: GQLTypeCache): GraphQLObjectType {
        return GraphQLObjectType.newObject()
                .name(type.java.simpleName)
                .description(getTypeDescription(type))
                .fields(asObjectFields(type, cache))
                .build()
    }

    fun asObjectFields(type: KClass<*>, cache: GQLTypeCache): List<GraphQLFieldDefinition> {
        val fields = mutableListOf<GraphQLFieldDefinition>()
        type.memberProperties.forEach { property ->
            val name = getPropertyName(property)
            val description = getPropertyDescription(property)
            val nullable = property.returnType.isMarkedNullable
            // Field builder
            val field = GraphQLFieldDefinition.newFieldDefinition()
                    .name(name)
                    .description(description)
            // Deprecation
            property.findAnnotation<Deprecated>()?.let {
                field.deprecate(it.message)
            }
            // Property type (JVM)
            val propertyType = property.returnType.javaType
            if (propertyType is Class<*>) {
                // Tries as scalar first
                val scalarType = getScalarType(propertyType)
                // Type
                val actualType: GraphQLOutputType = if (scalarType != null) {
                    scalarType
                } else if (propertyType is Map<*, *> || propertyType is Collection<*>) {
                    throw IllegalArgumentException("Maps and collections are not supported yet: ${property.name} in ${type.simpleName}")
                } else {
                    // Property type as Kotlin
                    val propertyKClass = Reflection.createKotlinClass(propertyType)
                    // Tries to convert to an object type
                    cache.getOrCreate(
                            propertyType.simpleName
                    ) { asObjectType(propertyKClass, cache) }
                }
                // Assignment
                fields += field.type(nullableType(actualType, nullable)).build()
            }
        }
        return fields
    }

    @JvmOverloads
    @Deprecated("Use Kotlin equivalent")
    fun asObjectType(type: Class<*>, cache: GQLTypeCache, exclusions: Set<String>? = null): GraphQLObjectType {
        return asObjectTypeBuilder(type, cache, exclusions).build()
    }

    @Deprecated("Use Kotlin equivalent")
    fun asObjectTypeBuilder(type: Class<*>, cache: GQLTypeCache, exclusions: Set<String>?): GraphQLObjectType.Builder {
        var builder: GraphQLObjectType.Builder = GraphQLObjectType.newObject()
                .name(type.simpleName)
        // Actual exclusions
        val actualExclusions = HashSet(DEFAULT_EXCLUSIONS)
        if (exclusions != null) {
            actualExclusions.addAll(exclusions)
        }
        // Gets the properties for the type
        for (descriptor in BeanUtils.getPropertyDescriptors(type)) {
            if (descriptor.readMethod != null) {
                val name = descriptor.name
                // Excludes some names by defaults
                if (!actualExclusions.contains(name)) {
                    val description = getDescription(type, descriptor)
                    val propertyType = descriptor.propertyType
                    val scalarType = getScalarType(propertyType)
                    if (scalarType != null) {
                        builder = builder.field { field ->
                            field
                                    .name(name)
                                    .description(description)
                                    .type(scalarType)
                        }
                    } else if (propertyType is Map<*, *> || propertyType is Collection<*>) {
                        throw IllegalArgumentException(
                                String.format(
                                        "Maps and collections are not supported yet: %s in %s",
                                        name,
                                        type.name
                                )
                        )
                    } else {
                        // Tries to convert to an object type
                        // Note: caching might be needed here...
                        val propertyObjectType = cache.getOrCreate(
                                propertyType.simpleName
                        ) { asObjectType(propertyType, cache) }
                        builder = builder.field { field ->
                            field
                                    .name(name)
                                    .description(description)
                                    .type(propertyObjectType)
                        }
                    }// Maps & collections not supported yet
                }
            }
        }
        // OK
        return builder
    }

    internal fun getScalarType(type: Class<*>): GraphQLScalarType? {
        return when {
            // Raw Kotlin
            Int::class.java.isAssignableFrom(type) -> GraphQLInt
            Long::class.java.isAssignableFrom(type) -> GraphQLLong
            Double::class.java.isAssignableFrom(type) -> GraphQLFloat
            Float::class.java.isAssignableFrom(type) -> GraphQLFloat
            Boolean::class.java.isAssignableFrom(type) -> GraphQLBoolean
            String::class.java.isAssignableFrom(type) -> GraphQLString
            // Java equivalents
            java.lang.Integer::class.java.isAssignableFrom(type) -> GraphQLInt
            java.lang.Long::class.java.isAssignableFrom(type) -> GraphQLLong
            java.lang.Double::class.java.isAssignableFrom(type) -> GraphQLFloat
            java.lang.Float::class.java.isAssignableFrom(type) -> GraphQLFloat
            java.lang.Boolean::class.java.isAssignableFrom(type) -> GraphQLBoolean
            java.lang.String::class.java.isAssignableFrom(type) -> GraphQLString
            // Custom scalar types
            JsonNode::class.java.isAssignableFrom(type) -> GQLScalarJSON.INSTANCE
            LocalDateTime::class.java.isAssignableFrom(type) -> GQLScalarLocalDateTime.INSTANCE
            else -> null
        }
    }

    fun getScalarType(type: KType): GraphQLScalarType? {
        val javaType = type.javaType
        return if (javaType is Class<*>) {
            getScalarType(javaType)
        } else {
            null
        }
    }

    fun <T> asObject(argument: Any?, type: Class<T>): T? {
        when (argument) {
            null -> return null
            is Map<*, *> -> {
                val map = argument as Map<*, *>?
                val o = type.getDeclaredConstructor().newInstance()
                for (descriptor in BeanUtils.getPropertyDescriptors(type)) {
                    val writeMethod = descriptor.writeMethod
                    if (writeMethod != null) {
                        val arg = map!![descriptor.name]
                        if (arg != null) {
                            try {
                                writeMethod.invoke(o, arg)
                            } catch (e: IllegalAccessException) {
                                throw IllegalStateException("Cannot set property " + descriptor.name)
                            } catch (e: InvocationTargetException) {
                                throw IllegalStateException("Cannot set property " + descriptor.name)
                            }

                        }
                    }
                }
                return o
            }
            else -> throw IllegalArgumentException("Argument is expected to be a map")
        }
    }
}

private fun getDescription(type: Class<*>, descriptor: PropertyDescriptor): String? {
    val readMethod: Method? = descriptor.readMethod
    if (readMethod != null) {
        val annotation: APIDescription? = readMethod.getAnnotation(APIDescription::class.java)
        if (annotation != null) {
            return annotation.value
        }
    }
    val field: Field? = FieldUtils.getField(type, descriptor.name, true)
    if (field != null) {
        val annotation = field.getAnnotation(APIDescription::class.java)
        if (annotation != null) {
            return annotation.value
        }
    }
    return descriptor.shortDescription
}
