package net.nemerosa.ontrack.extension.bitbucket.cloud.property

import net.nemerosa.ontrack.extension.git.model.GitConfiguration
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.model.support.UserPassword

class BitbucketCloudGitConfiguration(
    val property: BitbucketCloudProjectConfigurationProperty,
    override val configuredIssueService: ConfiguredIssueService?
) : GitConfiguration {

    override val type: String = "bitbucket-cloud"

    override val name: String = property.configuration.name

    override val remote: String =
        "https://bitbucket.org/${property.configuration.workspace}/${property.repository}.git"

    override val credentials: UserPassword =
        property.configuration.run {
            UserPassword(user, password ?: "")
        }

    override val commitLink: String =
        "https://bitbucket.org/${property.configuration.workspace}/${property.repository}/commits/{commit}"

    override val fileAtCommitLink: String =
        "https://bitbucket.org/${property.configuration.workspace}/${property.repository}/src/{commit}/{path}"

    override val indexationInterval: Int = property.indexationInterval

}