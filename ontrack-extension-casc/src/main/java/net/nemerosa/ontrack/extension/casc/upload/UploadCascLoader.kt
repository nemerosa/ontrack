package net.nemerosa.ontrack.extension.casc.upload

import net.nemerosa.ontrack.extension.casc.CascConfigurationProperties
import net.nemerosa.ontrack.extension.casc.CascLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UploadCascLoader(
    private val cascConfigurationProperties: CascConfigurationProperties,
    private val cascUploadService: CascUploadService,
) : CascLoader {

    private val logger: Logger = LoggerFactory.getLogger(UploadCascLoader::class.java)

    override fun loadCascFragments(): List<String> =
        if (cascConfigurationProperties.upload.enabled) {
            val yaml = cascUploadService.download()
            logger.info("Casc upload enabled, loading Yaml from storage (size = ${yaml?.length ?: 0}")
            listOfNotNull(yaml)
        } else {
            logger.info("Casc upload not enabled, not loading any Yaml.")
            emptyList()
        }
}