package net.nemerosa.ontrack.extension.slack.client

import com.slack.api.model.block.composition.MarkdownTextObject

/**
 * Abstraction of the Slack client.
 */
interface SlackClient {

    fun send(channel: String, markdown: MarkdownTextObject, iconEmoji: String?, color: String?): SlackClientResponse

}