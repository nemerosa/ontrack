package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.AccountGroup
import net.nemerosa.ontrack.model.security.AccountGroupMapping
import net.nemerosa.ontrack.model.security.AccountGroupMappingInput
import net.nemerosa.ontrack.model.security.AuthenticationSource
import net.nemerosa.ontrack.model.structure.ID

/**
 * Generic mapping for the account groups.
 */
interface AccountGroupMappingRepository {

    /**
     * For the given `mapping` type, collects the [AccountGroup] which are associated
     * with the `mappedName` name.
     *
     * @param  authenticationSource    Authentication source
     * @param origin Mapping name, for example: "Administrator"
     * @return List of mapped groups, can be empty, but not null
     */
    fun getGroups(authenticationSource: AuthenticationSource, origin: String): Collection<AccountGroup>

    /**
     * For the given `mapping` type, collects the [mappings][AccountGroupMapping].
     *
     * @param  authenticationSource    Authentication source
     * @return List of mappings, can be empty, but not null
     */
    fun getMappings(authenticationSource: AuthenticationSource): List<AccountGroupMapping>

    /**
     * Creates a new mapping
     *
     * @param  authenticationSource    Authentication source
     * @param input   Input data for the mapping
     * @return Created mapping
     */
    fun newMapping(authenticationSource: AuthenticationSource, input: AccountGroupMappingInput): AccountGroupMapping

    /**
     * Gets a mapping by its ID
     *
     * @param id ID of the mapping
     * @return Mapping
     */
    fun getMapping(id: ID): AccountGroupMapping

    /**
     * Updates a mapping
     *
     * @param id    ID of the mapping
     * @param input Input data for the mapping
     * @return Updated mapping
     */
    fun updateMapping(id: ID, input: AccountGroupMappingInput): AccountGroupMapping

    /**
     * Deletes a mapping
     *
     * @param id ID of the mapping
     * @return Acknowledgment
     */
    fun deleteMapping(id: ID): Ack

    /**
     * Gets the list of mappings for a given group.
     *
     * @param group Group to get the mappings to
     * @return List of mappings (never null)
     */
    fun getMappingsForGroup(group: AccountGroup): List<AccountGroupMapping>

    /**
     * Gets all mappings
     */
    fun findAll(): List<AccountGroupMapping>

    /**
     * Deletes all mappings associated to this [source].
     */
    fun deleteMappingsBySource(source: AuthenticationSource)
}