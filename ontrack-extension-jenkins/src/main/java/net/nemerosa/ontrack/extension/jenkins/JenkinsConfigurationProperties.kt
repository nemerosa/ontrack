package net.nemerosa.ontrack.extension.jenkins

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "ontrack.jenkins")
@Component
class JenkinsConfigurationProperties {

    /**
     * Default timeout to connect to Jenkins, in seconds
     */
    var timeout: Int = 30

}