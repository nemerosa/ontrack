package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLTypeReference;

public interface GQLInterface {

    GraphQLTypeReference getTypeRef();

    GraphQLInterfaceType createInterface();

}
