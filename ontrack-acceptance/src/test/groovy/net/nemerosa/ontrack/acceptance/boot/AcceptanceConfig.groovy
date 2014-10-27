package net.nemerosa.ontrack.acceptance.boot

import groovy.transform.ToString
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "ontrack")
@ToString(includeNames = true)
class AcceptanceConfig {

    String url = "http://localhost:8080"

    String getUrl() {
        return url
    }

    void setUrl(String url) {
        this.url = url
    }

    def setSystemProperties() {
        System.setProperty('ontrack.url', url)
    }
}
