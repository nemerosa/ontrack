package net.nemerosa.ontrack.model.security

interface GroupMappingService {

    /**
     * Given an IdP group, gets any corresponding mapped group.
     */
    fun getMappedGroup(idpGroup: String): AccountGroup?

    /**
     * Mapping an IdP group to a Yontrack group.
     */
    fun mapGroup(idpGroup: String, accountGroup: AccountGroup?)

    /**
     * Gets the list of all group mappings
     */
    val groupMappings: List<GroupMapping>

}