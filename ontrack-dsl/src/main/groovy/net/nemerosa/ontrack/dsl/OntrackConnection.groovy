package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.client.JsonClient
import net.nemerosa.ontrack.client.JsonClientImpl
import net.nemerosa.ontrack.client.OTHttpClient
import net.nemerosa.ontrack.client.OTHttpClientBuilder
import net.nemerosa.ontrack.dsl.client.OntrackResource
import org.apache.commons.lang3.StringUtils

class OntrackConnection {

    private final String url
    private boolean disableSsl = false
    private String user
    private String password

    private OntrackConnection(String url) {
        this.url = url
    }

    static OntrackConnection create(String url) {
        new OntrackConnection(url)
    }

    OntrackConnection disableSsl(boolean disableSsl) {
        this.disableSsl = disableSsl
        this
    }

    OntrackConnection authenticate(String user, String password) {
        this.user = user
        this.password = password
        this
    }

    Ontrack build() {
        def builder = OTHttpClientBuilder.create(url, disableSsl)
        // Credentials
        if (StringUtils.isNotBlank(user)) {
            builder = builder.withCredentials(user, password)
        }
        // Basic client
        OTHttpClient otHttpClient = builder.build()
        // Json client
        JsonClient jsonClient = new JsonClientImpl(otHttpClient)
        // Ontrack client
        new OntrackResource(jsonClient)
    }
}
