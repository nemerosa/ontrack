package net.nemerosa.ontrack.kdsl.connector.graphql

import com.apollographql.apollo.api.Mutation
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query

interface GraphQLConnector {

    fun <D : Operation.Data, T, V : Operation.Variables> query(query: Query<D, T, V>): T?

    fun <D : Operation.Data, T, V : Operation.Variables> mutate(
        mutation: Mutation<D, T, V>,
        userErrors: (T?) -> UserErrors?,
    ): T?

}