package net.nemerosa.ontrack.graphql.support

import com.fasterxml.jackson.databind.JsonNode
import graphql.Scalars.*
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputObjectType.newInputObject
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLScalarType
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

object GraphQLBeanConverter {

    private val DEFAULT_EXCLUSIONS = setOf(
            "class"
    )

    fun asInputType(type: Class<*>): GraphQLInputType {
        var builder: GraphQLInputObjectType.Builder = newInputObject()
                .name(type.simpleName)
        // Gets the properties for the type
        for (descriptor in BeanUtils.getPropertyDescriptors(type)) {
            if (descriptor.readMethod != null) {
                val name = descriptor.name
                val description = getDescription(type, descriptor)
                val scalarType = getScalarType(descriptor.propertyType)
                if (scalarType != null) {
                    builder = builder.field { field ->
                        field
                                .name(name)
                                .description(description)
                                .type(scalarType)
                    }
                }
            }
        }
        // OK
        return builder.build()
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

    @JvmOverloads
    fun asObjectType(type: Class<*>, cache: GQLTypeCache, exclusions: Set<String>? = null): GraphQLObjectType {
        return asObjectTypeBuilder(type, cache, exclusions).build()
    }

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

    private fun getScalarType(type: Class<*>): GraphQLScalarType? {
        return when {
            Int::class.java.isAssignableFrom(type) -> GraphQLInt
            Long::class.java.isAssignableFrom(type) -> GraphQLLong
            Double::class.java.isAssignableFrom(type) -> GraphQLFloat
            Float::class.java.isAssignableFrom(type) -> GraphQLFloat
            Boolean::class.java.isAssignableFrom(type) -> GraphQLBoolean
            String::class.java.isAssignableFrom(type) -> GraphQLString
            JsonNode::class.java.isAssignableFrom(type) -> GQLScalarJSON.INSTANCE
            LocalDateTime::class.java.isAssignableFrom(type) -> GQLScalarLocalDateTime.INSTANCE
            else -> null
        }
    }

    fun <T> asObject(argument: Any?, type: Class<T>): T? {
        when (argument) {
            null -> return null
            is Map<*, *> -> {
                val map = argument as Map<*, *>?
                val o = BeanUtils.instantiate(type)
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
