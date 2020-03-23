package net.nemerosa.ontrack.extension.support

import net.nemerosa.ontrack.model.extension.ExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.*

abstract class AbstractExtensionFeature
@JvmOverloads constructor(
        final override val id: String,
        override val name: String,
        override val description: String,
        override val options: ExtensionFeatureOptions = ExtensionFeatureOptions.DEFAULT
) : ExtensionFeature {

    final override var version: String

    companion object {
        private val logger = LoggerFactory.getLogger(ExtensionFeature::class.java)
        /**
         * Default version
         */
        const val VERSION_NONE = "none"
    }

    init {
        // Reads descriptor from resources
        val path = String.format("/META-INF/ontrack/extension/%s.properties", id)
        logger.info("[extension][{}] Loading meta information at {}", id, path)
        val ins = javaClass.getResourceAsStream(path)
        version = if (ins != null) {
            try {
                ins.use {
                    // Reads as properties
                    val properties = Properties()
                    properties.load(it)
                    // Reads the version information
                    val value = properties.getProperty("version")
                    if (StringUtils.isNotBlank(value)) {
                        value
                    } else {
                        logger.debug("[extension][{}] No version found in {} - using default version", id, path)
                        VERSION_NONE
                    }
                }
            } catch (ex: IOException) {
                throw RuntimeException(String.format("[extension][%s] Cannot read resource at %s", id, path), ex)
            }
        } else {
            logger.debug("[extension][{}] No meta information found at {} - using defaults", id, path)
            VERSION_NONE
        }
        logger.info("[extension][{}] Version = {}", id, version)
    }
}