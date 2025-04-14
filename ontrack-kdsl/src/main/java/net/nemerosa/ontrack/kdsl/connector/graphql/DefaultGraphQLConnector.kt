package net.nemerosa.ontrack.kdsl.connector.graphql

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Mutation
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.network.okHttpClient
import kotlinx.coroutines.runBlocking
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.JSON
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.LocalDateTime
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.UUID
import okhttp3.Interceptor
import okhttp3.OkHttpClient

class DefaultGraphQLConnector(
    connector: Connector,
    clientConfiguration: ApolloClient.Builder.() -> Unit = {},
) : GraphQLConnector {

    private val apolloClient: ApolloClient =
        ApolloClient.Builder()
            .serverUrl("${connector.url}/graphql")
            .addCustomScalarAdapter(LocalDateTime.type, localDateTimeCustomTypeAdapter)
            .addCustomScalarAdapter(UUID.type, uuidCustomTypeAdapter)
            .addCustomScalarAdapter(JSON.type, jsonCustomTypeAdapter)
            .okHttpClient(
                OkHttpClient.Builder()
                    .addInterceptor(
                        AuthorizationInterceptor(connector.token)
                    )
                    .build()
            )
            .apply {
                clientConfiguration()
            }
            .build()

    override fun <D : Query.Data> query(query: Query<D>): D? =
        runBlocking {
            apolloClient.query(query).execute().checkErrors().data
        }

    override fun <D : Mutation.Data> mutate(mutation: Mutation<D>, userErrors: (D?) -> UserErrors?): D? =
        runBlocking {
            val response = apolloClient.mutation(mutation).execute().checkErrors()
            val data = response.data
            checkErrors(userErrors(data))
            data
        }

    private fun checkErrors(userErrors: UserErrors?) {
        if (userErrors?.errors != null && userErrors.errors.isNotEmpty()) {
            val buffer = StringBuilder("Error(s) were returned:\n")
            userErrors.errors.forEachIndexed { index, error ->
                buffer.append("(${index + 1}) ${error.message}\n")
                if (!error.location.isNullOrBlank()) {
                    buffer.append("   Location: ${error.location}\n")
                }
                if (!error.exception.isNullOrBlank()) {
                    buffer.append("   Exception: ${error.exception}\n")
                }
            }
            throw GraphQLClientException(buffer.toString())
        }
    }

    private fun <D : Operation.Data> ApolloResponse<D>.checkErrors() = apply {
        val errors = this.errors
        if (!errors.isNullOrEmpty()) {
            throw GraphQLClientException.errors(errors)
        }
    }

    private class AuthorizationInterceptor(
        private val token: String?,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            return if (!token.isNullOrBlank()) {
                val request = chain.request().newBuilder()
                    .addHeader("X-Ontrack-Token", token)
                    .build()
                chain.proceed(request)
            } else {
                chain.proceed(chain.request())
            }
        }
    }
}