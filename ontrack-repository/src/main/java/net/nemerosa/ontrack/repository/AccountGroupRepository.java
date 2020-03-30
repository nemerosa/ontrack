package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.security.AccountGroup;
import net.nemerosa.ontrack.model.structure.ID;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface AccountGroupRepository {

    Collection<AccountGroup> findByAccount(int accountId);

    List<AccountGroup> findAll();

    AccountGroup newAccountGroup(AccountGroup group);

    AccountGroup getById(ID groupId);

    void update(AccountGroup group);

    Ack delete(ID groupId);

    void linkAccountToGroups(int accountId, @Nullable Collection<Integer> groupIds);

    List<AccountGroup> findByNameToken(String token);
}
