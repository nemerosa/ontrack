package net.nemerosa.ontrack.git

sealed interface GitRepositoryAuthenticator

class AnonymousGitRepositoryAuthenticator : GitRepositoryAuthenticator {
    companion object {
        val INSTANCE = AnonymousGitRepositoryAuthenticator()
    }
}

data class UsernamePasswordGitRepositoryAuthenticator(
    val username: String,
    val password: String,
) : GitRepositoryAuthenticator

data class TokenGitRepositoryAuthenticator(
    val token: String,
) : GitRepositoryAuthenticator

/**
 * This authenticator gives access to a token which may change at any time.
 */
data class AppTokenGitRepositoryAuthenticator(
    val tokenAccess: () -> String,
) : GitRepositoryAuthenticator
