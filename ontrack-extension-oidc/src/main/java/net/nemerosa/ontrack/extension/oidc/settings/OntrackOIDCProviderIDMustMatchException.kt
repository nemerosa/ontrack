package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.model.exceptions.InputException

class OntrackOIDCProviderIDMustMatchException(expected: String, actual: String) : InputException(
        "OIDC provider with id = $actual must match id = $expected."
)
