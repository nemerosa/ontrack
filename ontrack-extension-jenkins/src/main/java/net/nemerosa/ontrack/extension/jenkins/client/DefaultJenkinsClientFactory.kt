package net.nemerosa.ontrack.extension.jenkins.client

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfiguration
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration

@Component
@Profile("!${RunProfile.ACC}")
class DefaultJenkinsClientFactory(
    private val jenkinsConfigurationProperties: JenkinsConfigurationProperties,
) : JenkinsClientFactory {

    override fun getClient(configuration: JenkinsConfiguration): JenkinsClient {
        return DefaultJenkinsClient(
            url = configuration.url,
            client = RestTemplateBuilder()
                .rootUri(configuration.url)
                .basicAuthentication(configuration.user, configuration.password)
                .setReadTimeout(Duration.ofSeconds(jenkinsConfigurationProperties.timeout.toLong()))
                .build()
        )
    }

}
