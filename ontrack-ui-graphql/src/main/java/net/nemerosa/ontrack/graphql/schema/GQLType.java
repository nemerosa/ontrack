package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;

public interface GQLType {

    // FIXME Method to implement
    default String getTypeName() {
        return getTypeRef().getName();
    }

    // FIXME Method to set as default
    GraphQLTypeReference getTypeRef();

    GraphQLObjectType createType(GQLTypeCache cache);

}
