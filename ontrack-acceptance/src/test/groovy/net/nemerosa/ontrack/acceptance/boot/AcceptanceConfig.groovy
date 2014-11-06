package net.nemerosa.ontrack.acceptance.boot

import groovy.transform.ToString
import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "ontrack")
@ToString(includeNames = true)
class AcceptanceConfig {

    private String url = 'http://localhost:8080'
    private boolean disableSSL = false
    private String admin = 'admin'
    private Set<String> context = [] as Set

    String getUrl() {
        return url
    }

    void setUrl(String url) {
        this.url = url
    }

    boolean getDisableSSL() {
        return disableSSL
    }

    void setDisableSSL(boolean disableSSL) {
        this.disableSSL = disableSSL
    }

    Set<String> getContext() {
        return context
    }

    void setContext(Set<String> context) {
        this.context = context
    }

    def setSystemProperties() {
        System.setProperty 'ontrack.url', url
        System.setProperty 'ontrack.admin', admin
        System.setProperty 'ontrack.disableSSL', "${disableSSL}"
    }

    String getAdmin() {
        return admin
    }

    void setAdmin(String admin) {
        this.admin = admin
    }

    boolean acceptTest(AcceptanceTest acceptanceTest) {
        def excludes = acceptanceTest.excludes()
        context.disjoint(excludes as Set)
    }
}
