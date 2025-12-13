package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.message.GlobalMessage
import net.nemerosa.ontrack.model.message.GlobalMessageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/global-messages")
class GlobalMessagesController(
    private val globalMessageService: GlobalMessageService,
) {

    @GetMapping("")
    fun globalMessages(): GlobalMessages =
        GlobalMessages(globalMessageService.globalMessages)

    data class GlobalMessages(
        val messages: List<GlobalMessage>,
    )

}