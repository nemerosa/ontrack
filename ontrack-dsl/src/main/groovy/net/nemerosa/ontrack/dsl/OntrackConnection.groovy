package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.client.OntrackResource
import net.nemerosa.ontrack.dsl.http.OTHttpClientBuilder

class OntrackConnection {

    private final String url
    private boolean disableSsl = false
    private String user
    private String password
    private Closure logger

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

    OntrackConnection logger(Closure logger) {
        this.logger = logger
        this
    }

    OntrackConnection authenticate(String user, String password) {
        this.user = user
        this.password = password
        this
    }

    Ontrack build() {
        def builder = new OTHttpClientBuilder(url, disableSsl)
        // Credentials
        if (user) {
            builder = builder.withCredentials(user, password)
        }
        // Logger
        if (logger) {
            builder = builder.withLogger(logger)
        }
        // Ontrack client
        new OntrackResource(builder.build())
    }
}
