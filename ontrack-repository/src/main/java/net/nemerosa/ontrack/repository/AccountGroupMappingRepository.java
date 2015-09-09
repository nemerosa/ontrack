package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.security.AccountGroup;

import java.util.Collection;

/**
 * Generic mapping for the account groups.
 */
public interface AccountGroupMappingRepository {

    /**
     * For the given {@code mapping} type, collects the {@link AccountGroup} which are associated
     * with the {@code mappedName} name.
     *
     * @param mapping    Mapping type, for example: "ldap"
     * @param mappedName Mapping name, for example: "Administrator"
     * @return List of mapped groups, can be empty, but not null
     */
    Collection<AccountGroup> getGroups(String mapping, String mappedName);

}
