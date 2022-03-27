package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;

/**
 * @param <T> Type of runtime object to convert the argument into
 */
public interface GQLInputType<T> {

    @NotNull GraphQLInputType createInputType(@NotNull Set<GraphQLType> dictionary);

    @Nullable
    T convert(@Nullable Object argument);

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

    GraphQLTypeReference getTypeRef();
}
