package net.nemerosa.ontrack.extension.queue.ui

import com.fasterxml.jackson.databind.JsonNode

data class PostQueueInput(
        val processor: String,
        val payload: JsonNode,
)
