package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.annotations.APIDescription
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@APIDescription("Group description")
class AccountGroupInput(
    @get:NotNull(message = "The name is required.")
    @get:Size(min = 1, max = 40)
    @APIDescription("Name of the group")
    val name: String,
    @get:Size(min = 0, max = 300)
    @APIDescription("Description of the group")
    val description: String?,
)
