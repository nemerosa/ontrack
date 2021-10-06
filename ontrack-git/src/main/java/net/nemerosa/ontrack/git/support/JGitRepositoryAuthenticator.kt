package net.nemerosa.ontrack.git.support

import net.nemerosa.ontrack.git.AppTokenGitRepositoryAuthenticator
import net.nemerosa.ontrack.git.GitRepositoryAuthenticator
import net.nemerosa.ontrack.git.TokenGitRepositoryAuthenticator
import net.nemerosa.ontrack.git.UsernamePasswordGitRepositoryAuthenticator
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.Transport
import org.eclipse.jgit.transport.TransportHttp
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

fun GitRepositoryAuthenticator.getCredentialsProvider(): CredentialsProvider? = when (this) {
    is UsernamePasswordGitRepositoryAuthenticator ->
        UsernamePasswordCredentialsProvider(username, password)
    else -> null // Nothing by default
}

fun GitRepositoryAuthenticator.configureTransport(transport: Transport) {
    when (this) {
        is TokenGitRepositoryAuthenticator -> {
            configureHttpTransportWithToken(transport, token)
        }
        is AppTokenGitRepositoryAuthenticator -> {
            // See https://docs.github.com/en/developers/apps/building-github-apps/authenticating-with-github-apps#http-based-git-access-by-an-installation
            configureHttpTransportWithToken(transport, token)
        }
        else -> {
        } // Does nothing by default
    }
}

private fun configureHttpTransportWithToken(transport: Transport, token: String) {
    if (transport is TransportHttp) {
        transport.setAdditionalHeaders(
            mapOf(
                "x-access-token" to token
            )
        )
    }
}
