package net.nemerosa.ontrack.extension.license.control

import net.nemerosa.ontrack.extension.license.LicenseService
import net.nemerosa.ontrack.model.support.StartupService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class LicenseLoggingService(
    private val licenseService: LicenseService,
) : StartupService {

    private val logger: Logger = LoggerFactory.getLogger(LicenseLoggingService::class.java)

    override fun getName(): String = "License"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION + 1

    override fun start() {
        val license = licenseService.license
        license.apply {
            logger.info("[license] Type = $type")
            logger.info("[license] Name = $name")
            logger.info("[license] Assignee = $assignee")
            logger.info("[license] Active = $active")
            logger.info("[license] Valid until = $validUntil")
            logger.info("[license] Max. projects = $maxProjects")
            license.features.forEach { feature ->
                logger.info("[license] Feature = $feature")
            }
        }
    }
}