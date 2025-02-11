package net.nemerosa.ontrack.service.trigger

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.trigger.TriggerData
import net.nemerosa.ontrack.model.trigger.UserTrigger
import net.nemerosa.ontrack.model.trigger.UserTriggerData
import net.nemerosa.ontrack.model.trigger.createTriggerData
import org.springframework.stereotype.Component

@Component
class UserTriggerImpl(
    private val securityService: SecurityService,
) : UserTrigger {

    override val id: String = "user"

    override fun createUserTriggerData(): TriggerData =
        createTriggerData(
            data = UserTriggerData(
                username = securityService.currentAccount?.username
                    ?: error("Authentication is missing")
            )
        )

}
