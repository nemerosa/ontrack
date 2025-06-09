package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.message.GlobalMessageService
import net.nemerosa.ontrack.model.message.Message
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/global-messages")
class GlobalMessagesController(
    private val globalMessageService: GlobalMessageService,
) : AbstractResourceController() {

    @GetMapping("")
    fun globalMessages(): GlobalMessages =
        GlobalMessages(globalMessageService.globalMessages)

    data class GlobalMessages(
        val messages: List<Message>,
    )

}