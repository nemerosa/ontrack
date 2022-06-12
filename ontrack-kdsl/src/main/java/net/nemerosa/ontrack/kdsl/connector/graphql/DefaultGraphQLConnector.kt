package net.nemerosa.ontrack.kdsl.connector.graphql

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Mutation
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import kotlinx.coroutines.runBlocking
import net.nemerosa.ontrack.kdsl.connector.Connector
import okhttp3.Interceptor
import okhttp3.OkHttpClient

class DefaultGraphQLConnector(
    connector: Connector,
    clientConfiguration: ApolloClient.Builder.() -> Unit = {},
) : GraphQLConnector {

    private val apolloClient: ApolloClient =
        ApolloClient.builder()
            .serverUrl("${connector.url}/graphql")
            .addCustomTypeAdapter(LocalDateTimeCustomTypeAdapter.TYPE, LocalDateTimeCustomTypeAdapter())
            .addCustomTypeAdapter(UUIDCustomTypeAdapter.TYPE, UUIDCustomTypeAdapter())
            .addCustomTypeAdapter(JSONCustomTypeAdapter.TYPE, JSONCustomTypeAdapter())
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

    override fun <D : Operation.Data, T, V : Operation.Variables> query(query: Query<D, T, V>): T? =
        runBlocking {
            apolloClient.query(query).await().checkErrors().data
        }

    override fun <D : Operation.Data, T, V : Operation.Variables> mutate(
        mutation: Mutation<D, T, V>,
        userErrors: (T?) -> UserErrors?,
    ): T? =
        runBlocking {
            val response = apolloClient.mutate(mutation).await().checkErrors()
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

    private fun <T> Response<T>.checkErrors() = apply {
        val errors = this.errors
        if (errors != null && errors.isNotEmpty()) {
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