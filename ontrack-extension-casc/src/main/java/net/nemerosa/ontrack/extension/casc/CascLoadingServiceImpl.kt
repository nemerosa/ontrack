package net.nemerosa.ontrack.extension.casc

import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CascLoadingServiceImpl(
    private val cascService: CascService,
    private val cascLoaders: List<CascLoader>,
    private val securityService: SecurityService,
): CascLoadingService {

    private val logger: Logger = LoggerFactory.getLogger(CascLoadingServiceImpl::class.java)

    override fun load() {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        val fragments = cascLoaders.flatMap { loader ->
            logger.info("Casc loader: ${loader::class.java.name}")
            loader.loadCascFragments()
        }
        if (fragments.isEmpty()) {
            logger.info("No CasC resource is defined.")
        } else {
            logger.info("CasC resources loaded, running the configuration...")
            securityService.asAdmin {
                cascService.runYaml(fragments)
            }
            logger.info("CasC ran successfully")
        }
    }
}