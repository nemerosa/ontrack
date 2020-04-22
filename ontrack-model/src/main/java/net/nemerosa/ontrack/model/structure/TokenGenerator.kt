package net.nemerosa.ontrack.model.structure

/**
 * Service used to generate tokens
 */
interface TokenGenerator {
    /**
     * Generates a secure and unique token
     */
    fun generateToken(): String
}