package net.nemerosa.ontrack.extension.casc

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StartupService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * CasC loading at startup
 */
// @Component
class CascStartup(
    private val cascConfigurationProperties: CascConfigurationProperties,
    private val cascLoadingService: CascLoadingService,
    private val securityService: SecurityService,
) : StartupService {

    private val logger: Logger = LoggerFactory.getLogger(CascStartup::class.java)

    override fun getName(): String = "CasC"

    override fun startupOrder(): Int = StartupService.SYSTEM_REGISTRATION + 1

    override fun start() {
        logger.info("CasC startup")
        if (cascConfigurationProperties.enabled) {
            securityService.asAdmin {
                cascLoadingService.load()
            }
        } else {
            logger.info("CasC is disabled")
        }
    }
}