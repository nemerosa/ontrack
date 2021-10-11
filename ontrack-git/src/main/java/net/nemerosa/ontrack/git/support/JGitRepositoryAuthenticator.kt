package net.nemerosa.ontrack.git.support

import net.nemerosa.ontrack.git.AppTokenGitRepositoryAuthenticator
import net.nemerosa.ontrack.git.GitRepositoryAuthenticator
import net.nemerosa.ontrack.git.TokenGitRepositoryAuthenticator
import net.nemerosa.ontrack.git.UsernamePasswordGitRepositoryAuthenticator
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

fun GitRepositoryAuthenticator.getCredentialsProvider(): CredentialsProvider? = when (this) {
    is UsernamePasswordGitRepositoryAuthenticator ->
        UsernamePasswordCredentialsProvider(username, password)
    is TokenGitRepositoryAuthenticator ->
        tokenCredentialsProvider(token)
    is AppTokenGitRepositoryAuthenticator ->
        tokenCredentialsProvider(token)
    else -> null // Nothing by default
}

/**
 * See https://docs.github.com/en/developers/apps/building-github-apps/authenticating-with-github-apps#http-based-git-access-by-an-installation
 */
private fun tokenCredentialsProvider(token: String): CredentialsProvider =
    UsernamePasswordCredentialsProvider("x-access-token", token)
