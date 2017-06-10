package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.support.ApplicationInfo
import net.nemerosa.ontrack.model.support.ApplicationInfoProvider
import net.nemerosa.ontrack.model.support.ApplicationInfoService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ApplicationInfoServiceImpl
@Autowired constructor(val providers: List<ApplicationInfoProvider>) : ApplicationInfoService {

    private val logger = LoggerFactory.getLogger(ApplicationInfoService::class.java)

    init {
        providers.forEach { provider -> logger.info("[info] Info provided: {}", provider.javaClass.name) }
    }

    override fun getApplicationInfoList(): List<ApplicationInfo> = providers
            .flatMap { provider -> provider.applicationInfoList }
            .filter { it != null }
}