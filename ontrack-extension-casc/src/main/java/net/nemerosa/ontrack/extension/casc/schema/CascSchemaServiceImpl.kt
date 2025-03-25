package net.nemerosa.ontrack.extension.casc.schema

import net.nemerosa.ontrack.extension.casc.CascConfigurationProperties
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Service

@Service
class CascSchemaServiceImpl(
    private val cascConfigurationProperties: CascConfigurationProperties,
    private val securityService: SecurityService,
) : CascSchemaService {
    override val locations: List<String>
        get() {
            securityService.checkGlobalFunction(GlobalSettings::class.java)
            return cascConfigurationProperties.locations
        }
}