package net.nemerosa.ontrack.git

/**
 * Coordinates for a remote repository.
 *
 * @property type Type of repository (source of information)
 * @property name Name of the repository
 * @property remote Remote URL-ish for the repository.
 * @property authenticator Authentication mechanism to use for this repository
 */
class GitRepository(
    val type: String,
    val name: String,
    val remote: String,
    val authenticator: GitRepositoryAuthenticator? = null,
) {
    val id: String = (type + "_" + name + "_" + remote).replace("[:.\\\\/@]".toRegex(), "_")
}
