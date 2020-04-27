package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.ID

/**
 * Management of mappings between provided groups (by a LDAP system for example)
 * and actual Ontrack [group][AccountGroup].
 */
interface AccountGroupMappingService {
    /**
     * For the given [mapping][AuthenticationSource.id] type, collects the [AccountGroup] which are associated
     * with the [mappedName] name.
     *
     * @param mapping    Mapping type, for example: "ldap", mapped to [AuthenticationSource.id]
     * @param mappedName Mapping name, for example: "Administrator"
     * @return List of mapped groups
     */
    fun getGroups(mapping: String, mappedName: String): Collection<AccountGroup>

    /**
     * Gets ALL the mappings
     */
    val mappings: List<AccountGroupMapping>

    /**
     * For the given [mapping][AuthenticationSource.id] type, collects the [mappings][AccountGroupMapping].
     *
     * @param mapping Mapping type, for example: "ldap"
     * @return List of mappings
     */
    fun getMappings(mapping: String): List<AccountGroupMapping>

    /**
     * Creates a new mapping
     *
     * @param mapping Mapping type, for example: "ldap"
     * @param input   Input data for the mapping
     * @return Created mapping
     */
    fun newMapping(mapping: String, input: AccountGroupMappingInput): AccountGroupMapping

    /**
     * Gets a mapping by its ID
     *
     * @param mapping Mapping type, for example: "ldap"
     * @param id      ID of the mapping
     * @return Mapping
     */
    fun getMapping(mapping: String, id: ID): AccountGroupMapping

    /**
     * Updates a mapping
     *
     * @param mapping Mapping type, for example: "ldap"
     * @param id      ID of the mapping
     * @param input   Input data for the mapping
     * @return Updated mapping
     */
    fun updateMapping(mapping: String, id: ID, input: AccountGroupMappingInput): AccountGroupMapping

    /**
     * Deletes a mapping
     *
     * @param mapping Mapping type, for example: "ldap"
     * @param id      ID of the mapping
     * @return Acknowledgment
     */
    fun deleteMapping(mapping: String, id: ID): Ack

    /**
     * Gets the list of mappings for a given group.
     *
     * @param group Group to get the mappings to
     * @return List of mappings (never null)
     */
    fun getMappingsForGroup(group: AccountGroup): List<AccountGroupMapping>

}