package net.nemerosa.ontrack.extension.jenkins

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "ontrack.jenkins")
@Component
@APIName("Jenkins configuration")
@APIDescription("Configuration of the connection to Jenkins")
class JenkinsConfigurationProperties {

    @APIDescription("Default timeout to connect to Jenkins, in seconds")
    var timeout: Int = 30

}