package net.nemerosa.ontrack.extension.notifications.mail.mock

import net.nemerosa.ontrack.common.RunProfile
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Profile(RunProfile.DEV)
@RestController
@RequestMapping("/extension/notifications/mail/mock")
class MockMailController(
    private val mockMailService: MockMailService,
) {

    @GetMapping("find")
    fun find(
        @RequestParam to: String? = null,
        @RequestParam subject: String? = null,
    ): MockMail? =
        mockMailService.find(to, subject)

}