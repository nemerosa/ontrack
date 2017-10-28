package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;

public interface GQLType {

    String getTypeName();

    default GraphQLTypeReference getTypeRef() {
        return new GraphQLTypeReference(getTypeName());
    }

    GraphQLObjectType createType(GQLTypeCache cache);

}
