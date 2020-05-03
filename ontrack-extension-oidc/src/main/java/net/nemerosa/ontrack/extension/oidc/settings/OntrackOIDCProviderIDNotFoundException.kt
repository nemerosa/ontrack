package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class OntrackOIDCProviderIDNotFoundException(id: String) : NotFoundException(
        "OIDC provider with id = $id cannot be found."
)
