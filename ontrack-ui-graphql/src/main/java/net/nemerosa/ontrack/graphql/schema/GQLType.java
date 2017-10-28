package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;

public interface GQLType {

    GraphQLTypeReference getTypeRef();

    GraphQLObjectType createType();

}
