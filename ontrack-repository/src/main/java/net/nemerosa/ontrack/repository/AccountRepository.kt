package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountGroup
import net.nemerosa.ontrack.model.security.AuthenticationSource
import net.nemerosa.ontrack.model.structure.ID

interface AccountRepository {

    /**
     * Gets the list of all accounts
     */
    fun findAll(): Collection<Account>

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
    @Deprecated("Will be removed in V5")
    fun setPassword(accountId: Int, encodedPassword: String)

    /**
     * Loads an account by ID
     */
    fun getAccount(accountId: ID): Account

    /**
     * Looks for accounts based on some text.
     */
    fun findByNameToken(token: String): List<Account>

    /**
     * Gets the list of accounts associated with this account group.
     *
     * @param accountGroup                 Account group
     * @return List of accounts
     */
    fun getAccountsForGroup(accountGroup: AccountGroup): List<Account>

    /**
     * Finds an account using its name only.
     */
    fun findAccountByName(username: String): Account?

    /**
     * Checks if an account ID exists
     */
    fun doesAccountIdExist(id: ID): Boolean

    /**
     * Deletes all accounts having the given source.
     *
     * @param source Source to delete accounts from
     */
    @Deprecated("Will be removed in V5")
    fun deleteAccountBySource(source: AuthenticationSource)

    /**
     * Disabled / enables an account
     */
    @Deprecated("Will be removed in V5")
    fun setAccountDisabled(id: ID, disabled: Boolean)

    /**
     * Locks / unlocks an account
     */
    @Deprecated("Will be removed in V5")
    fun setAccountLocked(id: ID, locked: Boolean)

    /**
     * Finds or create the account
     */
    fun findOrCreateAccount(account: Account): Account
}