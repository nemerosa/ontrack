package net.nemerosa.ontrack.extension.general.message

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel
import net.nemerosa.ontrack.model.message.MessageType

data class SystemMessageSettings(
    @APIDescription("Message content")
    @APILabel("Message content")
    val content: String?,
    @APIDescription("Message type")
    @APILabel("Message type")
    val type: MessageType = MessageType.INFO,
)
