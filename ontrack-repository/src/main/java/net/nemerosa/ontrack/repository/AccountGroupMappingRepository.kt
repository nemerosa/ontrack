package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.security.AccountGroup;
import net.nemerosa.ontrack.model.security.AccountGroupMapping;
import net.nemerosa.ontrack.model.security.AccountGroupMappingInput;
import net.nemerosa.ontrack.model.structure.ID;

import java.util.Collection;
import java.util.List;

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

    /**
     * For the given {@code mapping} type, collects the {@linkplain AccountGroupMapping mappings}.
     *
     * @param mapping Mapping type, for example: "ldap"
     * @return List of mappings, can be empty, but not null
     */
    List<AccountGroupMapping> getMappings(String mapping);

    /**
     * Creates a new mapping
     *
     * @param mapping Mapping type, for example: "ldap"
     * @param input   Input data for the mapping
     * @return Created mapping
     */
    AccountGroupMapping newMapping(String mapping, AccountGroupMappingInput input);

    /**
     * Gets a mapping by its ID
     *
     * @param id ID of the mapping
     * @return Mapping
     */
    AccountGroupMapping getMapping(ID id);

    /**
     * Updates a mapping
     *
     * @param id    ID of the mapping
     * @param input Input data for the mapping
     * @return Updated mapping
     */
    AccountGroupMapping updateMapping(ID id, AccountGroupMappingInput input);

    /**
     * Deletes a mapping
     *
     * @param id ID of the mapping
     * @return Acknowledgment
     */
    Ack deleteMapping(ID id);

    /**
     * Gets the list of mappings for a given group.
     *
     * @param group Group to get the mappings to
     * @return List of mappings (never null)
     */
    List<AccountGroupMapping> getMappingsForGroup(AccountGroup group);
}
