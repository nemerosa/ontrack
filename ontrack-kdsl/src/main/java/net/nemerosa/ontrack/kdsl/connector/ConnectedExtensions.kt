package net.nemerosa.ontrack.kdsl.connector

import net.nemerosa.ontrack.kdsl.connector.graphql.DefaultGraphQLConnector
import net.nemerosa.ontrack.kdsl.connector.graphql.GraphQLConnector

/**
 * Creates a new GraphQL connector.
 */
val Connected.graphqlConnector: GraphQLConnector get() = DefaultGraphQLConnector(connector)