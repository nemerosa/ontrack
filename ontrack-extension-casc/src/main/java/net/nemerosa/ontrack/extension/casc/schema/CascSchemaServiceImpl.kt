package net.nemerosa.ontrack.extension.casc.schema

import net.nemerosa.ontrack.extension.casc.context.OntrackContext
import org.springframework.stereotype.Service

@Service
class CascSchemaServiceImpl(
    private val ontrackContext: OntrackContext,
): CascSchemaService {

    override val schema: CascType by lazy {
        cascObject(
            "CasC schema",
            "ontrack" to ontrackContext.with("Root of the configuration")
        )
    }
}