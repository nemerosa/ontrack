package net.nemerosa.ontrack.extension.casc

import net.nemerosa.ontrack.model.support.StartupService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component

/**
 * CasC loading at startup
 */
@Component
class CascStartup(
    private val cascConfigurationProperties: CascConfigurationProperties,
    private val resourceLoader: ResourceLoader,
    private val cascService: CascService,
) : StartupService {

    private val logger: Logger = LoggerFactory.getLogger(CascStartup::class.java)

    override fun getName(): String = "CasC"

    override fun startupOrder(): Int = StartupService.SYSTEM_REGISTRATION + 1

    override fun start() {
        logger.info("CasC startup")
        if (cascConfigurationProperties.enabled) {
            val locations = cascConfigurationProperties.locations
            if (locations.isEmpty()) {
                logger.info("No CasC resource is defined.")
                return
            }
            val parsedResources = locations.flatMap { location ->
                logger.info("CasC resource: $location")
                parseResource(location)
            }
            if (parsedResources.isNotEmpty()) {
                logger.info("CasC resources loaded, running the configuration")
                cascService.runYaml(parsedResources)
                logger.info("CasC ran successfully")
            } else {
                logger.info("No CasC resource was found.")
            }
        } else {
            logger.info("CasC is disabled")
        }
    }

    private fun parseResource(location: String): List<String> {
        val resource = resourceLoader.getResource(location)
        if (resource.isFile) {
            val file = resource.file
            if (file.exists() && file.isDirectory) {
                return file.listFiles()?.map {
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