package net.nemerosa.ontrack.extension.bitbucket.cloud.configuration

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.api.UserMenuExtensionGroups
import net.nemerosa.ontrack.extension.bitbucket.cloud.BitbucketCloudExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.support.Action
import net.nemerosa.ontrack.model.support.Action.Companion.of
import org.springframework.stereotype.Component

/**
 * Management of Bitbucket Cloud configurations available in the user menu.
 */
@Component
class BitbucketCloudConfigurationUserMenuExtension(
    feature: BitbucketCloudExtensionFeature
) : AbstractExtension(feature), UserMenuExtension {

    override val globalFunction: Class<out GlobalFunction?> = GlobalSettings::class.java

    override val action: Action =
        of("bitbucket-cloud-configurations", "Bitbucket Cloud configurations", "configurations")
            .withGroup(UserMenuExtensionGroups.configuration)

}