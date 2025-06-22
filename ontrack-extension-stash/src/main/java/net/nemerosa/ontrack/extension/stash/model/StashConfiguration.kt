package net.nemerosa.ontrack.extension.stash.model

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration

/**
 * @property name Name of this configuration
 * @property url Bitbucket URL
 * @property user User name
 * @property password User password
 * @property autoMergeToken Token used for approving pull requests for the auto merge operations
 */
class StashConfiguration(
    name: String,
    val url: String,
    user: String?,
    password: String?,
    @APILabel("Auto merge user")
    @APIDescription("Slug of the user approving pull requests for the auto merge operations")
    val autoMergeUser: String?,
    @APILabel("Auto merge token")
    @APIDescription("Token used for approving pull requests for the auto merge operations")
    val autoMergeToken: String?,
) : UserPasswordConfiguration<StashConfiguration>(name, user, password) {

    override fun obfuscate() = StashConfiguration(
        name = name,
        url = url,
        user = user,
        password = "",
        autoMergeUser = autoMergeUser,
        autoMergeToken = "",
    )

    override fun withPassword(password: String?): StashConfiguration {
        return StashConfiguration(
            name = name,
            url = url,
            user = user,
            password = password,
            autoMergeUser = autoMergeUser,
            autoMergeToken = autoMergeToken,
        )
    }

}
