package net.nemerosa.ontrack.graphql.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableSet;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLScalarType;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static graphql.Scalars.*;
import static graphql.schema.GraphQLInputObjectType.newInputObject;

public class GraphQLBeanConverter {

    private static final Set<String> DEFAULT_EXCLUSIONS = ImmutableSet.of(
            "class"
    );

    public static GraphQLInputType asInputType(Class<?> type) {
        GraphQLInputObjectType.Builder builder = newInputObject()
                .name(type.getSimpleName());
        // Gets the properties for the type
        for (PropertyDescriptor descriptor : BeanUtils.getPropertyDescriptors(type)) {
            if (descriptor.getReadMethod() != null) {
                String name = descriptor.getName();
                String description = descriptor.getShortDescription();
                GraphQLInputType scalarType = getScalarType(descriptor.getPropertyType());
                if (scalarType != null) {
                    builder = builder.field(field -> field
                            .name(name)
                            .description(description)
                            .type(scalarType)
                    );
                }
            }
        }
        // OK
        return builder.build();
    }

    public static GraphQLObjectType asObjectType(Class<?> type) {
        return asObjectType(type, null);
    }

    public static GraphQLObjectType asObjectType(Class<?> type, Set<String> exclusions) {
        GraphQLObjectType.Builder builder = GraphQLObjectType.newObject()
                .name(type.getSimpleName());
        // Actual exclusions
        Set<String> actualExclusions = new HashSet<>(DEFAULT_EXCLUSIONS);
        if (exclusions != null) {
            actualExclusions.addAll(exclusions);
        }
        // Gets the properties for the type
        for (PropertyDescriptor descriptor : BeanUtils.getPropertyDescriptors(type)) {
            if (descriptor.getReadMethod() != null) {
                String name = descriptor.getName();
                // Excludes some names by defaults
                if (!actualExclusions.contains(name)) {
                    String description = descriptor.getShortDescription();
                    Class<?> propertyType = descriptor.getPropertyType();
                    GraphQLScalarType scalarType = getScalarType(propertyType);
                    if (scalarType != null) {
                        builder = builder.field(field -> field
                                .name(name)
                                .description(description)
                                .type(scalarType)
                        );
                    }
                    // Maps & collections not supported yet
                    else if (Map.class.isAssignableFrom(propertyType) || Collection.class.isAssignableFrom(propertyType)) {
                        throw new IllegalArgumentException(
                                String.format(
                                        "Maps and collections are not supported yet: %s in %s",
                                        name,
                                        type.getName()
                                )
                        );
                    } else {
                        // Tries to convert to an object type
                        // Note: caching might be needed here...
                        GraphQLObjectType propertyObjectType = asObjectType(propertyType);
                        builder = builder.field(field -> field
                                .name(name)
                                .description(description)
                                .type(propertyObjectType)
                        );
                    }
                }
            }
        }
        // OK
        return builder.build();
    }

    public static GraphQLScalarType getScalarType(Class<?> type) {
        if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
            return GraphQLInt;
        } else if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)) {
            return GraphQLBoolean;
        } else if (String.class.isAssignableFrom(type)) {
            return GraphQLString;
        } else if (JsonNode.class.isAssignableFrom(type)) {
            return GQLScalarJSON.INSTANCE;
        } else if (LocalDateTime.class.isAssignableFrom(type)) {
            return GQLScalarLocalDateTime.INSTANCE;
        } else {
            return null;
        }
    }

    public static <T> T asObject(Object argument, Class<T> type) {
        if (argument == null) {
            return null;
        } else if (argument instanceof Map) {
            Map map = (Map) argument;
            T o = BeanUtils.instantiate(type);
            for (PropertyDescriptor descriptor : BeanUtils.getPropertyDescriptors(type)) {
                Method writeMethod = descriptor.getWriteMethod();
                if (writeMethod != null) {
                    Object arg = map.get(descriptor.getName());
                    try {
                        writeMethod.invoke(o, arg);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new IllegalStateException("Cannot set property " + descriptor.getName());
                    }
                }
            }
            return o;
        } else {
            throw new IllegalArgumentException("Argument is expected to be a map");
        }
    }
}
