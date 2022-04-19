package net.nemerosa.ontrack.extension.notifications.webhooks

data class WebhookPingPayloadData(
    val message: String,
) {
    companion object {
        /**
         * Ping payload type
         */
        const val TYPE = "ping"

        /**
         * Creating a ping webhook payload
         */
        fun pingPayload(message: String) = WebhookPayload<WebhookPingPayloadData>(
            type = TYPE,
            data = WebhookPingPayloadData(message)
        )
    }
}