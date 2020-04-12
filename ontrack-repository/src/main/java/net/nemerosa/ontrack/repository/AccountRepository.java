package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AccountGroup;
import net.nemerosa.ontrack.model.security.AuthenticationSource;
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider;
import net.nemerosa.ontrack.model.structure.ID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public interface AccountRepository {

    @Deprecated
    boolean checkPassword(int accountId, Predicate<String> check);

    @Deprecated
    Optional<Account> findUserByNameAndSource(String username, AuthenticationSourceProvider sourceProvider);

    @Nullable
    BuiltinAccount findBuiltinAccount(@NotNull String username);

    Collection<Account> findAll(Function<String, AuthenticationSource> authenticationSourceFunction);

    Account newAccount(Account account);

    void saveAccount(Account account);

    Ack deleteAccount(ID accountId);

    void setPassword(int accountId, String encodedPassword);

    Account getAccount(ID accountId, Function<String, AuthenticationSource> authenticationSourceFunction);

    List<Account> findByNameToken(String token, Function<String, AuthenticationSource> authenticationSourceFunction);

    /**
     * Gets the list of accounts associated with this account group.
     *
     * @param accountGroup                 Account group
     * @param authenticationSourceFunction Access to the authentication sources
     * @return List of accounts
     */
    List<Account> getAccountsForGroup(AccountGroup accountGroup, Function<String, AuthenticationSource> authenticationSourceFunction);
}
