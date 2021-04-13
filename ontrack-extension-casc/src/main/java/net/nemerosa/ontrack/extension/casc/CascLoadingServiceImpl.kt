package net.nemerosa.ontrack.extension.casc

import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CascLoadingServiceImpl(
    private val cascConfigurationProperties: CascConfigurationProperties,
    private val resourceLoader: ResourceLoader,
    private val cascService: CascService,
    private val securityService: SecurityService,
): CascLoadingService {

    private val logger: Logger = LoggerFactory.getLogger(CascLoadingServiceImpl::class.java)

    override fun load() {
        load(cascConfigurationProperties.locations)
    }

    override fun load(locations: List<String>) {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
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
            securityService.asAdmin {
                cascService.runYaml(parsedResources)
            }
            logger.info("CasC ran successfully")
        } else {
            logger.info("No CasC resource was found.")
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