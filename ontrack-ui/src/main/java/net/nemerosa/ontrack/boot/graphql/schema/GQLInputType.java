package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.GraphQLInputType;

/**
 * @param <T> Type of runtime object to convert the argument into
 */
public interface GQLInputType<T> {

    GraphQLInputType getInputType();

    T convert(Object argument);
}
