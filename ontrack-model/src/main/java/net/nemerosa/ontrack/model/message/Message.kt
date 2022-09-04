package net.nemerosa.ontrack.model.message

data class Message(
    val content: String,
    val type: MessageType,
)