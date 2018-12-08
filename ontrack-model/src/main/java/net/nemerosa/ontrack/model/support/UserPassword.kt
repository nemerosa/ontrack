package net.nemerosa.ontrack.model.support

/**
 * Support class for a user name associated with a password
 */
class UserPassword(
        val user: String,
        val password: String
) {
    override fun toString(): String {
        return user
    }
}
