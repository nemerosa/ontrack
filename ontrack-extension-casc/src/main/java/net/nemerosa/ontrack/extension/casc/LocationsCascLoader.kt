package net.nemerosa.ontrack.extension.casc

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component

/**
 * Loads the Casc documents from the locations defined in the configuration.
 */
@Component
class LocationsCascLoader(
    private val resourceLoader: ResourceLoader,
    private val cascConfigurationProperties: CascConfigurationProperties,
) : CascLoader {

    private val logger: Logger = LoggerFactory.getLogger(LocationsCascLoader::class.java)

    override fun loadCascFragments(): List<String> =
        load(cascConfigurationProperties.locations)

    private fun load(locations: List<String>): List<String> = locations.flatMap { location ->
        logger.info("CasC resource: $location")
        parseResource(location)
    }

    private fun parseResource(location: String): List<String> {
        val resource = resourceLoader.getResource(location)
        if (resource.isFile) {
            val file = resource.file
            if (file.exists() && file.isDirectory) {
                logger.info("CasC resource directory: $file")
                return file.listFiles()?.filter {
                    it.isFile && it.canRead() && it.extension in setOf("yml", "yaml")
                }?.map {
                    logger.info("CasC resource file: $it")
                    it.readText()
                } ?: emptyList()
            }
        }
        if (!resource.exists()) {
            error("Cannot find CasC resource at $location")
        }
        return listOf(
            resource.inputStream.use {
                it.bufferedReader().readText()
            }
        )
    }
}