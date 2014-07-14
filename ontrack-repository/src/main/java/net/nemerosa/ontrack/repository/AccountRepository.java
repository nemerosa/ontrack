package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.security.Account;

import java.util.Optional;
import java.util.function.Predicate;

public interface AccountRepository {

    boolean checkPassword(int accountId, Predicate<String> check);

    Optional<Account> findUserByNameAndMode(String username, String mode);
}
