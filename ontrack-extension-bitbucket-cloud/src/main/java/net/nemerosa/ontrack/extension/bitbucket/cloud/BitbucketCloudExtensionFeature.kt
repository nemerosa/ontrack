package net.nemerosa.ontrack.extension.bitbucket.cloud

import net.nemerosa.ontrack.extension.git.GitExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class BitbucketCloudExtensionFeature(
    gitExtensionFeature: GitExtensionFeature
) : AbstractExtensionFeature(
    "bitbucket-cloud", "Bitbucket Cloud", "Support for Atlassian Bitbucket Cloud",
    ExtensionFeatureOptions.DEFAULT
        .withGui(true)
        .withDependency(gitExtensionFeature)
)
