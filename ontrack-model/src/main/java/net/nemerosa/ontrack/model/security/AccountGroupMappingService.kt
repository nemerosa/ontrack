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
     * @param authenticationSource    Authentication source
     * @param mappedName Mapping name, for example: "Administrator"
     * @return List of mapped groups
     */
    fun getGroups(authenticationSource: AuthenticationSource, mappedName: String): Collection<AccountGroup>

    /**
     * Gets ALL the mappings
     */
    val mappings: List<AccountGroupMapping>

    /**
     * For the given [mapping][AuthenticationSource.id] type, collects the [mappings][AccountGroupMapping].
     *
     * @param authenticationSource    Authentication source
     * @return List of mappings
     */
    fun getMappings(authenticationSource: AuthenticationSource): List<AccountGroupMapping>

    /**
     * Creates a new mapping
     *
     * @param authenticationSource Authentication source
     * @param input   Input data for the mapping
     * @return Created mapping
     */
    fun newMapping(authenticationSource: AuthenticationSource, input: AccountGroupMappingInput): AccountGroupMapping

    /**
     * Gets a mapping by its ID
     *
     * @param authenticationSource Authentication source
     * @param id      ID of the mapping
     * @return Mapping
     */
    fun getMapping(authenticationSource: AuthenticationSource, id: ID): AccountGroupMapping

    /**
     * Updates a mapping
     *
     * @param authenticationSource Authentication source
     * @param id      ID of the mapping
     * @param input   Input data for the mapping
     * @return Updated mapping
     */
    fun updateMapping(authenticationSource: AuthenticationSource, id: ID, input: AccountGroupMappingInput): AccountGroupMapping

    /**
     * Deletes a mapping
     *
     * @param authenticationSource Authentication source
     * @param id      ID of the mapping
     * @return Acknowledgment
     */
    fun deleteMapping(authenticationSource: AuthenticationSource, id: ID): Ack

    /**
     * Gets the list of mappings for a given group.
     *
     * @param group Group to get the mappings to
     * @return List of mappings (never null)
     */
    fun getMappingsForGroup(group: AccountGroup): List<AccountGroupMapping>

    /**
     * Deletes all mappings associated to this [source].
     */
    fun deleteMappingsBySource(source: AuthenticationSource)

}