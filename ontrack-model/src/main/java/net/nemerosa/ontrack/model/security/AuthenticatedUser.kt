package net.nemerosa.ontrack.model.security

import java.security.Principal

interface AuthenticatedUser : AuthorisationsCheck, Principal {

    val account: Account?
    val groups: List<AuthorizedGroup>

}
