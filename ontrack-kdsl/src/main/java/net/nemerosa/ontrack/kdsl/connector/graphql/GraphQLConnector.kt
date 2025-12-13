package net.nemerosa.ontrack.kdsl.connector.graphql

import com.apollographql.apollo.api.Mutation
import com.apollographql.apollo.api.Query

interface GraphQLConnector {

    fun <D : Query.Data> query(query: Query<D>): D?

    fun <D : Mutation.Data> mutate(
        mutation: Mutation<D>,
        userErrors: (D?) -> UserErrors?,
    ): D?

}