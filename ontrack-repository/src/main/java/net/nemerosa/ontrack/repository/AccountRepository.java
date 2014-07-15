package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AuthenticationSource;
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface AccountRepository {

    boolean checkPassword(int accountId, Predicate<String> check);

    Optional<Account> findUserByNameAndSource(String username, AuthenticationSourceProvider sourceProvider);

    Collection<Account> findAll(Function<String, AuthenticationSource> authenticationSourceFunction);

    Account newAccount(Account account);

    void setPassword(int accountId, String encodedPassword);
}
