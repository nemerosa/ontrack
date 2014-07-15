package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider;

import java.util.Optional;
import java.util.function.Predicate;

public interface AccountRepository {

    boolean checkPassword(int accountId, Predicate<String> check);

    Optional<Account> findUserByNameAndSource(String username, AuthenticationSourceProvider sourceProvider);
}
