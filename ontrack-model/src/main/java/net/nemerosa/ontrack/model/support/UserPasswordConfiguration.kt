package net.nemerosa.ontrack.model.support

@Deprecated("The methods in this interface will be refactored in V5.")
interface UserPasswordConfiguration<T : UserPasswordConfiguration<T>> : CredentialsConfiguration<T> {

    /**
     * User name
     */
    val user: String?

    /**
     * Plain password
     */
    val password: String?

    /**
     * Replacing the password with a new one
     */
    fun withPassword(password: String?): T

}
