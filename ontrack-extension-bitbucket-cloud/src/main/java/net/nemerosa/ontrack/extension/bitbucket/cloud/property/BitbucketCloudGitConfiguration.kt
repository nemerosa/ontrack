package net.nemerosa.ontrack.extension.bitbucket.cloud.property

import net.nemerosa.ontrack.extension.git.model.GitConfiguration
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.model.support.UserPassword
import java.util.*

class BitbucketCloudGitConfiguration(
    val property: BitbucketCloudProjectConfigurationProperty,
    private val configuredIssueService: ConfiguredIssueService?
) : GitConfiguration {

    override fun getType(): String = "bitbucket-cloud"

    override fun getName(): String = property.configuration.name

    override fun getRemote(): String =
        "https://bitbucket.org/${property.configuration.workspace}/${property.repository}.git"

    override fun getCredentials(): Optional<UserPassword> =
        property.configuration.credentials

    override fun getCommitLink(): String =
        "https://bitbucket.org/${property.configuration.workspace}/${property.repository}/commits/{commit}"

    override fun getFileAtCommitLink(): String =
        "https://bitbucket.org/${property.configuration.workspace}/${property.repository}/src/{commit}/{path}"

    override fun getIndexationInterval(): Int = property.indexationInterval

    override fun getConfiguredIssueService(): Optional<ConfiguredIssueService> =
        Optional.ofNullable(configuredIssueService)

}