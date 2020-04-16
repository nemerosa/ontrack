package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountGroup
import net.nemerosa.ontrack.model.security.AuthenticationSource
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider
import net.nemerosa.ontrack.model.structure.ID
import java.util.*

interface AccountRepository {

    @Deprecated("")
    fun findUserByNameAndSource(username: String, sourceProvider: AuthenticationSourceProvider): Optional<Account>

    /**
     * Gets a built-in if it exists. The criteria is based on the [username] and on the [mode][Account.authenticationSource]
     * being stored.
     */
    fun findBuiltinAccount(username: String): BuiltinAccount?

    /**
     * Gets the list of all accounts
     */
    fun findAll(authenticationSourceFunction: (String) -> AuthenticationSource): Collection<Account>

    /**
     * Creates a new account
     */
    fun newAccount(account: Account): Account

    /**
     * Edits an existing account
     */
    fun saveAccount(account: Account)

    /**
     * Deletes an account
     */
    fun deleteAccount(accountId: ID): Ack

    /**
     * Changes the password of an account
     */
    fun setPassword(accountId: Int, encodedPassword: String)

    /**
     * Loads an account by ID
     */
    fun getAccount(accountId: ID, authenticationSourceFunction: (String) -> AuthenticationSource): Account

    /**
     * Looks for accounts based on some text.
     */
    fun findByNameToken(token: String, authenticationSourceFunction: (String) -> AuthenticationSource): List<Account>

    /**
     * Gets the list of accounts associated with this account group.
     *
     * @param accountGroup                 Account group
     * @param authenticationSourceFunction Access to the authentication sources
     * @return List of accounts
     */
    fun getAccountsForGroup(accountGroup: AccountGroup, authenticationSourceFunction: (String) -> AuthenticationSource): List<Account>
}