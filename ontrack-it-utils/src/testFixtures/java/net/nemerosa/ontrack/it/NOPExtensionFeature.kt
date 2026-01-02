package net.nemerosa.ontrack.it

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import org.springframework.stereotype.Component

@Component
class NOPExtensionFeature : AbstractExtensionFeature(
        "nop",
        "NOP",
        "NOP extension"
)
