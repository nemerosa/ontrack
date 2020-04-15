package net.nemerosa.ontrack.model.security

class AuthorizedAccount(
        val account: Account,
        val authorisations: AuthorisationsCheck
) : AuthorisationsCheck {

    override fun isGranted(fn: Class<out GlobalFunction>): Boolean =
            (SecurityRole.ADMINISTRATOR == account.role) || authorisations.isGranted(fn)

    override fun isGranted(projectId: Int, fn: Class<out ProjectFunction>): Boolean =
            (SecurityRole.ADMINISTRATOR == account.role) || authorisations.isGranted(projectId, fn)

}
