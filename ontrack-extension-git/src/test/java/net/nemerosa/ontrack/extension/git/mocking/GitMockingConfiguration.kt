package net.nemerosa.ontrack.extension.git.mocking

import net.nemerosa.ontrack.extension.git.model.GitConfiguration
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.git.GitRepositoryAuthenticator

class GitMockingConfiguration : GitConfiguration {

    override val configuredIssueService: ConfiguredIssueService? = null

    override val remote: String = "uri:test:git"

    override val name: String = "Mocking"

    override val authenticator: GitRepositoryAuthenticator? = null

    override val type: String = "mocking"

    override val commitLink: String =
        TODO("not implemented")

    override val fileAtCommitLink: String =
        TODO("Not yet implemented")

    override val indexationInterval: Int =
        TODO("Not yet implemented")
}