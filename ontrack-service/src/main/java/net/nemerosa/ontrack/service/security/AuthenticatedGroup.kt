package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.AccountGroup
import net.nemerosa.ontrack.model.security.Authorisations
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.security.ProjectFunction

class AuthenticatedGroup(
        val group: AccountGroup,
        val authorisations: Authorisations
) {

    fun isGranted(fn: Class<out GlobalFunction>): Boolean = authorisations.isGranted(fn)

    fun isGranted(projectId: Int, fn: Class<out ProjectFunction>): Boolean = authorisations.isGranted(projectId, fn)

}
