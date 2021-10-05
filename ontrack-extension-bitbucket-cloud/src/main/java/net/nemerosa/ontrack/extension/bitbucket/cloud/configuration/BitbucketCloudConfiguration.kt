package net.nemerosa.ontrack.extension.bitbucket.cloud.configuration

import net.nemerosa.ontrack.model.support.ConfigurationDescriptor
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration

/**
 * Connection configuration to Bitbucket Cloud, at workspace level.
 *
 * @property name Name of this configuration
 * @property workspace Slug of the Bitbucket Cloud workspace to connect to
 * @property user Name of the user used to connect to Bitbucket Cloud
 * @property password App password used to connect to Bitbucket Cloud
 */
// TODO #532 Workaround
open class BitbucketCloudConfiguration(
    override val name: String,
    val workspace: String,
    override val user: String,
    override val password: String?
) : UserPasswordConfiguration<BitbucketCloudConfiguration> {

    override val descriptor: ConfigurationDescriptor get() = ConfigurationDescriptor(name, "$name ($workspace)")

    override fun obfuscate() = BitbucketCloudConfiguration(
        name = name,
        workspace = workspace,
        user = user,
        password = ""
    )

    override fun withPassword(password: String?) = BitbucketCloudConfiguration(
        name = name,
        workspace = workspace,
        user = user,
        password = password ?: ""
    )

}