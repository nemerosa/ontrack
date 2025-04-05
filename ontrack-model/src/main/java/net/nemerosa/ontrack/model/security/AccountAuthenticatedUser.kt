package net.nemerosa.ontrack.model.security

class AccountAuthenticatedUser(
    override val account: Account,
    val authorisations: Authorisations,
    val groups: List<AuthorizedGroup>,
) : AuthenticatedUser {

    override fun isGranted(fn: Class<out GlobalFunction>): Boolean {
        // TODO
        return true
    }

    override fun isGranted(projectId: Int, fn: Class<out ProjectFunction>): Boolean {
        // TODO
        return true
    }

    override fun getName(): String = account.fullName

}