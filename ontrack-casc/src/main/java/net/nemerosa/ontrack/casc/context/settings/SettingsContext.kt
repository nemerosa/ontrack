package net.nemerosa.ontrack.casc.context.settings

import net.nemerosa.ontrack.casc.context.AbstractHolderContext
import net.nemerosa.ontrack.casc.context.SubCascContext
import net.nemerosa.ontrack.casc.context.SubConfigContext
import org.springframework.stereotype.Component

@Component
class SettingsContext(
    subContexts: List<SubSettingsContext>,
) : AbstractHolderContext<SubSettingsContext>(
    subContexts,
    "Management of settings"
), SubConfigContext {

    override val field: String = "settings"

}

interface SubSettingsContext : SubCascContext

