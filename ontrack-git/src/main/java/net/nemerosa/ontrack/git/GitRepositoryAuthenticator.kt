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

data class AppTokenGitRepositoryAuthenticator(
    val token: String,
) : GitRepositoryAuthenticator
