package net.nemerosa.ontrack.extension.slack.client

import com.slack.api.model.block.composition.MarkdownTextObject
import net.nemerosa.ontrack.common.RunProfile
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(RunProfile.ACC)
class MockSlackClient : SlackClient {

    private val messages = mutableListOf<MockSlackMessage>()

    override fun send(
        channel: String,
        markdown: MarkdownTextObject,
        iconEmoji: String?,
        color: String?,
    ): SlackClientResponse {
        messages += MockSlackMessage(
            channel = channel,
            color = color,
            iconEmoji = iconEmoji,
            markdown = markdown.text,
        )
        return SlackClientResponse(
            ok = true,
            error = null,
        )
    }

    fun getChannelMessages(channel: String): List<MockSlackMessage> =
        messages.filter { it.channel.trimStart('#') == channel }

}