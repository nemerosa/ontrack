package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.graphql.payloads.PayloadInterface
import net.nemerosa.ontrack.graphql.payloads.PayloadUserError
import net.nemerosa.ontrack.graphql.payloads.toPayloadErrors
import net.nemerosa.ontrack.model.security.UserService
import net.nemerosa.ontrack.model.support.PasswordChange
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller

@Controller
class UserProfileController(
    private val userService: UserService,
) {

    @MutationMapping
    fun changePassword(@Argument input: ChangePasswordInput): ChangePasswordPayload =
        try {
            userService.changePassword(PasswordChange(input.oldPassword, input.newPassword))
            ChangePasswordPayload(emptyList())
        } catch (any: Exception) {
            ChangePasswordPayload(any.toPayloadErrors())
        }

}

data class ChangePasswordInput(
    val oldPassword: String,
    val newPassword: String,
)

class ChangePasswordPayload(
    errors: List<PayloadUserError>? = null,
) : PayloadInterface(errors)

