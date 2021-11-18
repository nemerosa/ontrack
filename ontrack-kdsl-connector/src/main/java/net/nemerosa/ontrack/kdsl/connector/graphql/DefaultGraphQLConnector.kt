package net.nemerosa.ontrack.kdsl.connector.graphql

import com.apollographql.apollo.ApolloClient
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
    clientConfiguration: ApolloClient.Builder.() -> Unit = {}
) : GraphQLConnector {

    private val apolloClient: ApolloClient =
        ApolloClient.builder()
            .serverUrl("${connector.url}/graphql")
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