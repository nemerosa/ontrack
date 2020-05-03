package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.model.exceptions.InputException

class OntrackOIDCProviderIDAlreadyExistsException(id: String) : InputException(
        "OIDC provider with id = $id already exists."
)
