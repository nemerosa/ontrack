package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputType;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;

/**
 * @param <T> Type of runtime object to convert the argument into
 */
public interface GQLInputType<T> {

    GraphQLInputType getInputType();

    T convert(Object argument);

    static GraphQLInputObjectField formField(String fieldName, String description) {
        return formField(fieldName, description, GraphQLString);
    }

    static GraphQLInputObjectField formField(String fieldName, String description, GraphQLInputType inputType) {
        return newInputObjectField()
                .name(fieldName)
                .description(description)
                .type(inputType)
                .build();
    }

}
