package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.common.api.APIDescription

class MessageProperty(
    @APIDescription("Type of message")
    val type: MessageType,
    @APIDescription("Content of the message")
    val text: String,
)