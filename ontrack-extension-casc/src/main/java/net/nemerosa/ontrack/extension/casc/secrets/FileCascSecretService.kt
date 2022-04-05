package net.nemerosa.ontrack.extension.casc.secrets

import net.nemerosa.ontrack.extension.casc.CascConfigurationProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException

@Component
@ConditionalOnProperty(
    prefix = "ontrack.config.casc.secrets",
    name = ["type"],
    havingValue = "file",
    matchIfMissing = false,
)
class FileCascSecretService(
    cascConfigurationProperties: CascConfigurationProperties,
) : CascSecretService {

    private val logger: Logger = LoggerFactory.getLogger(FileCascSecretService::class.java)

    private val directory: File by lazy {
        val path = cascConfigurationProperties.secrets.directory
        logger.info("Using directory $path")
        if (path.isBlank()) {
            throw FileCascSecretServiceNoDirectoryException()
        } else {
            val dir = File(path)
            if (!dir.exists() || !dir.isDirectory) {
                throw FileCascSecretServiceInvalidDirectoryException(path)
            } else {
                dir
            }
        }
    }

    override fun getValue(ref: String): String {
        val base = ref.substringBefore(".")
        val name = ref.substringAfter(".")
        if (base.isBlank() || name.isBlank()) {
            throw FileCascSecretServiceSecretFormatException(ref)
        }
        val dir = File(directory, base)
        if (!dir.exists() || !dir.isDirectory) {
            throw FileCascSecretServiceSecretNotFoundException(
                """Cannot get the [$ref] secret because path at [$dir] does not exist or is not a directory."""
            )
        }
        val file = File(dir, name)
        return if (file.exists() && file.isFile && file.canRead()) {
            try {
                file.readText()
            } catch (any: IOException) {
                throw FileCascSecretServiceSecretCannotReadException(ref, any)
            }
        } else {
            throw FileCascSecretServiceSecretNotFoundException(
                """Cannot get the [$ref] secret because path at [$file] does not exist, is not a file or cannot be read."""
            )
        }
    }
}