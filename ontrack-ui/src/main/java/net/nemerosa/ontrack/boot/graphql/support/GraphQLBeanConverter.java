package net.nemerosa.ontrack.boot.graphql.support;

import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static graphql.Scalars.*;
import static graphql.schema.GraphQLInputObjectType.newInputObject;

public class GraphQLBeanConverter {

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

    public static GraphQLInputType getScalarType(Class<?> type) {
        if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
            return GraphQLInt;
        } else if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)) {
            return GraphQLBoolean;
        } else if (String.class.isAssignableFrom(type)) {
            return GraphQLString;
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
