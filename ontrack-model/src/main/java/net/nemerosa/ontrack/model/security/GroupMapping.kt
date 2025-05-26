package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Mapping between an IdP group and a Yontrack group")
data class GroupMapping(
    @APIDescription("IdP group name")
    val idpGroup: String,
    @APIDescription("Yontrack group")
    val group: AccountGroup,
)
