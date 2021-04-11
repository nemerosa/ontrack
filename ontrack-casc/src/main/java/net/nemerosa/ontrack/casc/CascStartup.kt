package net.nemerosa.ontrack.casc

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
): StartupService {

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
            val parsedResources = locations.map { location ->
                logger.info("CasC resource: $location")
                parseResource(location)
            }
            logger.info("CasC resources loaded, running the configuration")
            cascService.runYaml(parsedResources)
            logger.info("CasC ran successfully")
        } else {
            logger.info("CasC is disabled")
        }
    }

    private fun parseResource(location: String): String {
        val resource = resourceLoader.getResource(location)
        if (!resource.exists()) {
            error("Cannot find CasC resource at $location")
        }
        return resource.inputStream.use {
            it.bufferedReader().readText()
        }
    }
}