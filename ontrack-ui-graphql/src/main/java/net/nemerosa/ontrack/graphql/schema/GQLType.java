package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;

public interface GQLType {

    default GraphQLTypeReference getTypeRef() {
        return new GraphQLTypeReference(getClass().getSimpleName());
    }

    GraphQLObjectType createType();

}
