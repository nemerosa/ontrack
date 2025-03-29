package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;

import jakarta.validation.constraints.NotNull;

public interface GQLType {

    @NotNull String getTypeName();

    default GraphQLTypeReference getTypeRef() {
        return new GraphQLTypeReference(getTypeName());
    }

    @NotNull GraphQLObjectType createType(@NotNull GQLTypeCache cache);

}
