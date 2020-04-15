package net.nemerosa.ontrack.model.security

class AuthorizedGroup(
        val group: AccountGroup,
        val authorisations: AuthorisationsCheck
) : AuthorisationsCheck {

    override fun isGranted(fn: Class<out GlobalFunction>): Boolean = authorisations.isGranted(fn)

    override fun isGranted(projectId: Int, fn: Class<out ProjectFunction>): Boolean = authorisations.isGranted(projectId, fn)

}
