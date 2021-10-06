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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserPasswordConfiguration<*>) return false

        if (name != other.name) return false
        if (user != other.user) return false
        if (password != other.password) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (user?.hashCode() ?: 0)
        result = 31 * result + (password?.hashCode() ?: 0)
        return result
    }

}
