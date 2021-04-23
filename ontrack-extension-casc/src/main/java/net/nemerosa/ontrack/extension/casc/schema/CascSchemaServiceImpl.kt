package net.nemerosa.ontrack.extension.casc.schema

import net.nemerosa.ontrack.extension.casc.CascConfigurationProperties
import net.nemerosa.ontrack.extension.casc.context.OntrackContext
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Service

@Service
class CascSchemaServiceImpl(
    private val cascConfigurationProperties: CascConfigurationProperties,
    private val securityService: SecurityService,
    private val ontrackContext: OntrackContext,
) : CascSchemaService {

    override val schema: CascType
        get() {
            securityService.checkGlobalFunction(GlobalSettings::class.java)
            return internalSchema
        }

    private val internalSchema: CascType by lazy {
        cascObject(
            "CasC schema",
            "ontrack" to ontrackContext.with("Root of the configuration")
        )
    }

    override val locations: List<String>
        get() {
            securityService.checkGlobalFunction(GlobalSettings::class.java)
            return cascConfigurationProperties.locations
        }

}