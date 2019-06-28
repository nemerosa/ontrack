package net.nemerosa.ontrack.extension.support.client

import net.nemerosa.ontrack.client.JsonClient
import net.nemerosa.ontrack.client.JsonClientImpl
import net.nemerosa.ontrack.client.OTHttpClient
import net.nemerosa.ontrack.client.OTHttpClientBuilder
import org.springframework.stereotype.Component

@Component
class DefaultClientFactory : ClientFactory {

    override fun getJsonClient(clientConnection: ClientConnection): JsonClient {
        // Gets a HTTP client
        val httpClient = getHttpClient(clientConnection)
        // Builds a JSON client on top of it
        return JsonClientImpl(httpClient)
    }

    override fun getHttpClient(clientConnection: ClientConnection): OTHttpClient {
        return OTHttpClientBuilder.create(clientConnection.url, false)
                // Basic credentials
                .withCredentials(clientConnection.user, clientConnection.password)
                // Timeout
                .withTimeoutSeconds(clientConnection.timeoutSeconds ?: 60)
                // OK
                .build()
    }
}
