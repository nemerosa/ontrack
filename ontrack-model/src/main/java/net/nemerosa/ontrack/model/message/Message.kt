package net.nemerosa.ontrack.model.message

import java.time.LocalDateTime

data class Message(
    val id: String,
    val datetime: LocalDateTime,
    val content: String,
    val type: MessageType,
)