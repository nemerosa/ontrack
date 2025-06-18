package net.nemerosa.ontrack.extension.bitbucket.cloud.configuration

import net.nemerosa.ontrack.model.support.UserPasswordConfiguration

/**
 * Connection configuration to Bitbucket Cloud, at workspace level.
 *
 * @property name Name of this configuration
 * @property workspace Slug of the Bitbucket Cloud workspace to connect to
 * @property user Name of the user used to connect to Bitbucket Cloud
 * @property password App password used to connect to Bitbucket Cloud
 */
class BitbucketCloudConfiguration(
    name: String,
    val workspace: String,
    user: String,
    password: String?
) : UserPasswordConfiguration<BitbucketCloudConfiguration>(name, user, password) {

    override fun obfuscate() = BitbucketCloudConfiguration(
        name = name,
        workspace = workspace,
        user = user ?: "",
        password = ""
    )

    override fun withPassword(password: String?) = BitbucketCloudConfiguration(
        name = name,
        workspace = workspace,
        user = user ?: "",
        password = password ?: ""
    )

}