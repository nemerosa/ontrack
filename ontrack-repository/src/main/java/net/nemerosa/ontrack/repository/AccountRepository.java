package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.security.Account;

import java.util.function.Predicate;

public interface AccountRepository {

    boolean checkPassword(int accountId, Predicate<String> check);

    Account findUserByNameAndMode(String username, String mode);
}
