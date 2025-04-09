package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.UserService
import net.nemerosa.ontrack.model.support.PasswordChange
import org.springframework.stereotype.Service

@Service
class UserServiceImpl : UserService {

    @Deprecated("Will be removed in V5")
    override fun changePassword(input: PasswordChange): Ack {
        error("Changing the password in Ontrack is no longer supported")
    }

}