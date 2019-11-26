package net.nemerosa.ontrack.extension.issues

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class IssuesExtensionFeature : AbstractExtensionFeature(
        "issues",
        "Issues",
        "Access to issues and ticketing systems",
        ExtensionFeatureOptions.DEFAULT.withGui(true)
)
