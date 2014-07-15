package net.nemerosa.ontrack.model.security;

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
}
