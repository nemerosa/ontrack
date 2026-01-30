package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.common.api.APIDescription

data class UserTemplatingFunctionParameters(
    @APIDescription("Field to display for the user. Defaults to the username.")
    val field: UserTemplatingFunctionField? = null,
)
