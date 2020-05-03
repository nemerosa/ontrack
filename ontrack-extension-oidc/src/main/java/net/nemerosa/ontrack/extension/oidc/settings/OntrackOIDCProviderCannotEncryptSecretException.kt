package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.model.exceptions.InputException

class OntrackOIDCProviderCannotEncryptSecretException : InputException(
        "Issue while encrypting the client secret."
)
