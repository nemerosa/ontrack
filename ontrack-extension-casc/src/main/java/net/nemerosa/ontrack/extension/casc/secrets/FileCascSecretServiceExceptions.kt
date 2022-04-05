package net.nemerosa.ontrack.extension.casc.secrets

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.model.exceptions.InputException
import java.io.IOException

class FileCascSecretServiceNoDirectoryException : BaseException(
    """
        When using the file-based Casc secret service, the ontrack.config.casc.secrets.directory property (or 
        ONTRACK_CONFIG_CASC_SECRETS_DIRECTORY) must be the path to an existing directory.
    """.trimIndent()
)

class FileCascSecretServiceInvalidDirectoryException(path: String) : BaseException(
    """
        When using the secret confidential store, the ontrack.config.casc.secrets.directory property (or 
        ONTRACK_CONFIG_CASC_SECRETS_DIRECTORY) must be the path to an existing directory: $path
    """.trimIndent()
)

class FileCascSecretServiceSecretFormatException(ref: String) : InputException(
    """Secrets must be expressed using `base.name`, not `$ref`."""
)

class FileCascSecretServiceSecretNotFoundException(message: String) : BaseException(message)

class FileCascSecretServiceSecretCannotReadException(ref: String, any: IOException) : BaseException(
    any,
    """Cannot access the secret [$ref]."""
)
