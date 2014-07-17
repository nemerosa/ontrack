package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.security.AccountGroup;

import java.util.Collection;
import java.util.List;

public interface AccountGroupRepository {

    Collection<AccountGroup> findByAccount(int accountId);

    List<AccountGroup> findAll();

    AccountGroup newAccountGroup(AccountGroup group);
}
