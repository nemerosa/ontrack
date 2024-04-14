package net.nemerosa.ontrack.extension.slack.client

import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import com.slack.api.model.Attachment
import com.slack.api.model.block.Blocks.asBlocks
import com.slack.api.model.block.Blocks.section
import com.slack.api.model.block.composition.MarkdownTextObject

class DefaultSlackClient(slackToken: String, endpointUrl: String?) : SlackClient {

    private val methods: MethodsClient = Slack.getInstance().methods(slackToken)

    init {
        if (!endpointUrl.isNullOrBlank()) {
            methods.endpointUrlPrefix = endpointUrl
        }
    }

    override fun send(
        channel: String,
        markdown: MarkdownTextObject,
        iconEmoji: String?,
        color: String?
    ): SlackClientResponse {
        val response = methods.chatPostMessage { req ->
            req
                .channel(channel)
                .attachments(
                    listOf(
                        Attachment.builder()
                            .run {
                                if (color.isNullOrBlank()) {
                                    this
                                } else {
                                    color(color)
                                }
                            }
                            .blocks(
                                asBlocks(
                                    section { section ->
                                        section.text(markdown)
                                    }
                                )
                            )
                            .build()
                    )
                )
                .run {
                    if (iconEmoji.isNullOrBlank()) {
                        this
                    } else {
                        iconEmoji(iconEmoji)
                    }
                }
        }
        return SlackClientResponse(
            ok = response.isOk,
            error = response.error,
        )
    }
}