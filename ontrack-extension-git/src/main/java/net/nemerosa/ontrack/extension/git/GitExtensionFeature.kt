package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.stale.StaleExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GitExtensionFeature(
        scmExtensionFeature: SCMExtensionFeature,
        staleExtensionFeature: StaleExtensionFeature
) : AbstractExtensionFeature(
        "git",
        "Git",
        "Support for Git",
        ExtensionFeatureOptions.DEFAULT
                .withGui(true)
                .withDependency(scmExtensionFeature)
                .withDependency(staleExtensionFeature)
)