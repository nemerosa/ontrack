package net.nemerosa.ontrack.model.security

import kotlin.random.Random

/**
 * Service used to store and restore an authentication across boundaries (like queuing)
 */
interface AuthenticationStorageService {

    /**
     * Gets the ID of the current authentication so that it can be restored later on.
     *
     * Fails if no authentication is available.
     */
    fun getAccountId(): String

    /**
     * Given an account ID, executes some code with the associated account ID.
     */
    fun withAccountId(accountId: String, code: () -> Unit)

    companion object {
        /**
         * Known ID of the run-as admin
         */
        val RUN_AS_ADMINISTRATOR_ACCOUNT_ID = Random.nextInt(1_000_000).toString(10)
    }
}