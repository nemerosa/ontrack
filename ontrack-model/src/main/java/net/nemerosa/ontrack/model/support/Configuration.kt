package net.nemerosa.ontrack.model.support

interface Configuration<T : Configuration<T>> {

    /**
     * Name of this configuration
     */
    val name: String

    /**
     * Utility method that erases any sensitive data from this configuration
     * and returns new one.
     */
    fun obfuscate(): T

    /**
     * Given an [old configuration][oldConfig], injects its credentials into the current
     * configuration if they are not defined and returns it.
     *
     * @param oldConfig Old version of this configuration
     * @return New version of this configuration where credentials have been replaced by the old ones
     * if the new ones are not provided.
     *
     * Returns `this` is there is nothing to inject.
     */
    fun injectCredentials(oldConfig: T): T

    /**
     * Prepares a configuration for encryption.
     *
     * Returns `this` is there is nothing to encrypt.
     */
    fun encrypt(crypting: (plain: String?) -> String?): T

    /**
     * Prepares a configuration for decryption.
     *
     * Returns `this` is there is nothing to decrypt.
     */
    fun decrypt(decrypting: (encrypted: String?) -> String?): T

}