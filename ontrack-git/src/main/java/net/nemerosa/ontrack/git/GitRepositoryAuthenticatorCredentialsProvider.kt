package net.nemerosa.ontrack.git

import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

fun GitRepositoryAuthenticator.getCredentialsProvider(): CredentialsProvider? = when (this) {
    is AnonymousGitRepositoryAuthenticator ->
        null
    is UsernamePasswordGitRepositoryAuthenticator ->
        UsernamePasswordCredentialsProvider(username, password)
    is TokenGitRepositoryAuthenticator ->
        TODO("JGit + Header needs to be supported, or the URL must be hacked")
    is AppTokenGitRepositoryAuthenticator ->
        TODO("JGit + Header needs to be supported, or the URL must be hacked")
}
