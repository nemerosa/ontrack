package net.nemerosa.ontrack.model.message

data class GlobalMessage(
    val featureId: String,
    val content: String,
    val type: MessageType,
)