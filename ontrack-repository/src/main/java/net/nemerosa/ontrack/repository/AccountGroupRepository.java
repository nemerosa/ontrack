package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.security.AccountGroup;

import java.util.Collection;

public interface AccountGroupRepository {

    Collection<AccountGroup> findByAccount(int accountId);

}
