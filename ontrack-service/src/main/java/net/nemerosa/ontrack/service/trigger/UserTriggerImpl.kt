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
    override val displayName: String = "User"

    override fun createUserTriggerData(): TriggerData =
        createTriggerData(
            data = UserTriggerData(
                username = securityService.currentUser?.name
                    ?: error("Authentication is missing")
            )
        )

    override fun filterCriteria(token: String, criterias: MutableList<String>, params: MutableMap<String, Any?>) {
        criterias += "TRIGGER_DATA::JSONB->>'username' = :username"
        params["username"] = token
    }

}
