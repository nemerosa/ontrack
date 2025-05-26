package net.nemerosa.ontrack.model.security

import java.security.Principal

interface AuthenticatedUser : AuthorisationsCheck, Principal {

    val account: Account?
    val groups: List<AuthorizedGroup>
    val assignedGroups: List<AccountGroup>
    val mappedGroups: List<AccountGroup>
    val idpGroups: List<String>

}
