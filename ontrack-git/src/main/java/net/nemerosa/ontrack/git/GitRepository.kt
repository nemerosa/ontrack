package net.nemerosa.ontrack.git

/**
 * Coordinates for a remote repository.
 *
 * @property type Type of repository (source of information)
 * @property name Name of the repository
 * @property remote Remote URL-ish for the repository.
 * @property user User. Blank or `null` when no authorisation is needed.
 * @property password Password for the user
 */
class GitRepository(
        val type: String,
        val name: String,
        val remote: String,
        val user: String?,
        val password: String?
) {
    val id: String = (type + "_" + name + "_" + remote).replace("[:.\\\\/@]".toRegex(), "_")
}
