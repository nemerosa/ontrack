package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.message.GlobalMessageService
import net.nemerosa.ontrack.model.message.Message
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder

@RestController
@RequestMapping("/rest/global-messages")
class GlobalMessagesController(
    private val globalMessageService: GlobalMessageService,
) : AbstractResourceController() {

    @GetMapping("")
    fun globalMessages(): Resource<GlobalMessages> = Resource.of(
        GlobalMessages(globalMessageService.globalMessages),
        uri(MvcUriComponentsBuilder.on(javaClass).globalMessages())
    )

    data class GlobalMessages(
        val messages: List<Message>,
    )

}