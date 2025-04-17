package net.nemerosa.ontrack.extension.slack.client

import net.nemerosa.ontrack.common.RunProfile
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(RunProfile.DEV)
class MockSlackClientFactory(
    private val mockSlackClient: MockSlackClient,
) : SlackClientFactory {

    override fun getSlackClient(slackToken: String, endpointUrl: String?): SlackClient = mockSlackClient

}