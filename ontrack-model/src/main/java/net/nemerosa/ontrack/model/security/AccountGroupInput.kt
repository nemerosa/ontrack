package net.nemerosa.ontrack.model.security

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class AccountGroupInput(
        @get:NotNull(message = "The name is required.")
        @get:Size(min = 1, max = 40)
        val name: String,
        @get:Size(min = 0, max = 300)
        val description: String?,
        val autoJoin: Boolean
)
