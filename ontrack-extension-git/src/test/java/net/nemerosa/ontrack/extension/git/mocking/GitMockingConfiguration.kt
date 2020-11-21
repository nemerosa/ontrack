package net.nemerosa.ontrack.extension.git.mocking

import net.nemerosa.ontrack.extension.git.model.GitConfiguration
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.model.support.UserPassword
import java.util.*

class GitMockingConfiguration: GitConfiguration {

    override fun getConfiguredIssueService(): Optional<ConfiguredIssueService> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRemote(): String = "uri:test:git"

    override fun getName(): String = "Mocking"

    override fun getCredentials(): Optional<UserPassword> = Optional.empty()

    override fun getType(): String = "mocking"

    override fun getFileAtCommitLink(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCommitLink(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getIndexationInterval(): Int = 0
}