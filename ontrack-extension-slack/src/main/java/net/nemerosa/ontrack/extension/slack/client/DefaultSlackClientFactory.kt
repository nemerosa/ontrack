package net.nemerosa.ontrack.extension.slack.client

import net.nemerosa.ontrack.common.RunProfile
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(RunProfile.PROD)
class DefaultSlackClientFactory: SlackClientFactory {

    override fun getSlackClient(slackToken: String, endpointUrl: String?): SlackClient =
        DefaultSlackClient(slackToken, endpointUrl)

}