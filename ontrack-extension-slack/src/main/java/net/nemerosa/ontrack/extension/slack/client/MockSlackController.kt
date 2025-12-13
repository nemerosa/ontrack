package net.nemerosa.ontrack.extension.slack.client

import net.nemerosa.ontrack.common.RunProfile
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Profile(RunProfile.DEV)
@RestController
@RequestMapping("/extension/slack/mock")
class MockSlackController(
    private val mockSlackClient: MockSlackClient,
) {

    @GetMapping("channel/{channel}")
    fun getChannelMessages(@PathVariable channel: String): List<MockSlackMessage> =
        mockSlackClient.getChannelMessages(channel)


}