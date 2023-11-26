package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.exceptions.InputException

class TokenGenerationNameAlreadyExistsException(name: String) : InputException(
    "Token with name $name already exists."
)
