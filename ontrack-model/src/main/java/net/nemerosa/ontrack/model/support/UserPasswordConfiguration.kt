package net.nemerosa.ontrack.model.support

abstract class UserPasswordConfiguration<T : UserPasswordConfiguration<T>>(
    /**
     * Configuration name
     */
    override val name: String,
    /**
     * User name
     */
    val user: String?,
    /**
     * Plain password
     */
    val password: String?,
) : CredentialsConfiguration<T> {

    override fun injectCredentials(oldConfig: T): T {
        return if (oldConfig.user == this.user && this.password.isNullOrBlank()) {
            withPassword(oldConfig.password)
        } else {
            @Suppress("UNCHECKED_CAST")
            this as T
        }
    }

    override fun encrypt(crypting: (plain: String?) -> String?): T =
        withPassword(crypting(password))

    override fun decrypt(decrypting: (encrypted: String?) -> String?): T =
        withPassword(decrypting(password))

    /**
     * Replacing the password with a new one
     */
    abstract fun withPassword(password: String?): T

}
