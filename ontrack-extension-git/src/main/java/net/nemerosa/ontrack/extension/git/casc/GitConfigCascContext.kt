package net.nemerosa.ontrack.extension.git.casc

import net.nemerosa.ontrack.extension.casc.context.AbstractHolderContext
import net.nemerosa.ontrack.extension.casc.context.extensions.SubExtensionsContext
import org.springframework.stereotype.Component

@Component
class GitConfigCascContext(
    subContexts: List<GitConfigSubCascContext>,
) : AbstractHolderContext<GitConfigSubCascContext>(
    subContexts = subContexts,
    description = "Configuration for the Git extension"
), SubExtensionsContext {

    override val field: String = "git"

}