package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.http.OTHttpClientBuilder

class OntrackConnection {

    private final String url
    private boolean disableSsl = false
    private String user
    private String password
    private String token
    private OntrackLogger logger
    private int maxTries = 1
    private int retryDelaySeconds = 10

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

    OntrackConnection maxTries(int value) {
        this.maxTries = value
        this
    }

    OntrackConnection retryDelaySeconds(int value) {
        this.retryDelaySeconds = value
        this
    }

    OntrackConnection logger(OntrackLogger logger) {
        this.logger = logger
        this
    }

    OntrackConnection authenticate(String user, String password) {
        this.user = user
        this.password = password
        this
    }

    /**
     * Uses a token-based authentication
     * @param token Token to use
     * @return This connection
     */
    OntrackConnection authenticate(String token) {
        this.token = token
        this
    }

    Ontrack build() {
        def builder = new OTHttpClientBuilder(url, disableSsl)
        // Credentials
        if (user) {
            builder = builder.withCredentials(user, password)
        }
        // Token
        if (token) {
            builder = builder.withToken("X-Ontrack-Token", token)
        }
        // Logger
        if (logger) {
            builder = builder.withLogger({ String it -> logger.trace(it) })
        }
        // Retries
        if (maxTries > 1) {
            builder = builder.withMaxTries(maxTries).withRetryDelaySeconds(retryDelaySeconds)
        }
        // Ontrack client
        new Ontrack(builder.build())
    }
}
