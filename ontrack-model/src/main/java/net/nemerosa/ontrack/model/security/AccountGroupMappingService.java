package net.nemerosa.ontrack.model.security;

import java.util.Collection;
import java.util.List;

/**
 * Generic mapping for the account groups.
 */
public interface AccountGroupMappingService {

    /**
     * For the given {@code mapping} type, collects the {@link AccountGroup} which are associated
     * with the {@code mappedName} name.
     *
     * @param mapping    Mapping type, for example: "ldap"
     * @param mappedName Mapping name, for example: "Administrator"
     * @return List of mapped groups, can be empty, but not null
     */
    Collection<AccountGroup> getGroups(String mapping, String mappedName);

    /**
     * For the given {@code mapping} type, collects the {@linkplain AccountGroupMapping mappings}.
     *
     * @param mapping Mapping type, for example: "ldap"
     * @return List of mappings, can be empty, but not null
     */
    List<AccountGroupMapping> getMappings(String mapping);
}
