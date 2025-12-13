package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.security.AccountGroup
import net.nemerosa.ontrack.model.security.GroupMapping

interface GroupMappingRepository {

    fun mapGroup(idpGroup: String, groupID: Int?)

    fun getMappedGroupId(idpGroup: String): Int?

    fun getMappings(groupFn: (Int) -> AccountGroup): List<GroupMapping>

}