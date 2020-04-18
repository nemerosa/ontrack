package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ApplicationInfo
import net.nemerosa.ontrack.model.support.ApplicationInfoProvider
import net.nemerosa.ontrack.model.support.ApplicationInfoService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ApplicationInfoServiceImpl(
        private val providers: List<ApplicationInfoProvider>,
        private val securityService: SecurityService
) : ApplicationInfoService {

    private val logger = LoggerFactory.getLogger(ApplicationInfoService::class.java)

    init {
        providers.forEach { provider -> logger.info("[info] Info provided: {}", provider.javaClass.name) }
    }

    override val applicationInfoList: List<ApplicationInfo>
        get() {
            securityService.checkAuthenticated()
            return providers
                    .flatMap { provider -> provider.applicationInfoList }
                    .filterNotNull()
        }
}