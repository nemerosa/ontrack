package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AuthenticationSource;
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider;
import net.nemerosa.ontrack.model.structure.ID;

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

    void saveAccount(Account account);

    Ack deleteAccount(ID accountId);

    void setPassword(int accountId, String encodedPassword);

    Account getAccount(ID accountId, Function<String, AuthenticationSource> authenticationSourceFunction);
}
