package net.nemerosa.ontrack.casc.context

import org.springframework.stereotype.Component

@Component
class ConfigContext(subContexts: List<SubConfigContext>) : AbstractHolderContext<SubConfigContext>(subContexts)

interface SubConfigContext : SubCascContext

@Component
class NOPSubConfigContext : NOPSubCascContext(), SubConfigContext
