package net.nemerosa.ontrack.casc.schema

import net.nemerosa.ontrack.casc.context.OntrackContext
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