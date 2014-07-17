package net.nemerosa.ontrack.model.security;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.structure.ID;

import java.util.Collection;
import java.util.List;

/**
 * Management of accounts.
 */
public interface AccountService {

    /**
     * Completes an account with the list of its authorisations.
     *
     * @param raw Account without authorisations
     * @return Account with authorisations
     */
    Account withACL(Account raw);

    /**
     * List of accounts
     */
    List<Account> getAccounts();

    /**
     * Creates a built-in account
     */
    Account create(AccountInput input);

    /**
     * Gets an account using its ID
     */
    Account getAccount(ID accountId);

    /**
     * Updating an existing account
     *
     * @param accountId ID of the account to update
     * @param input     Update data
     * @return Updated account
     */
    Account updateAccount(ID accountId, AccountInput input);

    /**
     * Deletes an account
     */
    Ack deleteAccount(ID accountId);

    /**
     * List of account groups
     */
    List<AccountGroup> getAccountGroups();
}
