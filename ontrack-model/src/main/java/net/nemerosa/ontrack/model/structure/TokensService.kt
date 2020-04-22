package net.nemerosa.ontrack.model.structure

/**
 * Management of account tokens.
 */
interface TokensService {

    /**
     * Generates a new token for the current user
     */
    fun generateNewToken(): String

}
