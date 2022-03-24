package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.common.BaseException
import java.io.File

abstract class AbstractSecretFileConfidentialStoreException(message: String) : BaseException(message)

class SecretFileConfidentialStoreNoDirectoryException : AbstractSecretFileConfidentialStoreException(
    """
        When using the secret confidential store, the ontrack.config.file-key-store.directory property (or 
        ONTRACK_CONFIG_FILE_KEY_STORE_DIRECTORY) must be the path to an existing directory.
    """.trimIndent()
)

class SecretFileConfidentialStoreInvalidDirectoryException(path: String) : AbstractSecretFileConfidentialStoreException(
    """
        When using the secret confidential store, the ontrack.config.file-key-store.directory property (or 
        ONTRACK_CONFIG_FILE_KEY_STORE_DIRECTORY) must be the path to an existing directory: $path
    """.trimIndent()
)

class SecretFileConfidentialStoreMissingKeyFileException(file: File) : AbstractSecretFileConfidentialStoreException(
    """
        Key file at ${file.absolutePath} must exist, must be a file and must be readable.
    """.trimIndent()
)

class SecretFileConfidentialStoreReadOnlyException(key: String) : AbstractSecretFileConfidentialStoreException(
    """
        The secret store is read-only (incoming key: $key).
    """.trimIndent()
)