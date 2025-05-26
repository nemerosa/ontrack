package net.nemerosa.ontrack.model.security

class AccountAuthenticatedUser(
    override val account: Account,
    val authorisations: Authorisations,
    override val groups: List<AuthorizedGroup>,
    override val assignedGroups: List<AccountGroup>,
    override val mappedGroups: List<AccountGroup>,
    override val idpGroups: List<String>,
) : AuthenticatedUser {

    override fun isGranted(fn: Class<out GlobalFunction>): Boolean =
        authorisations.isGranted(fn) || groups.any { it.isGranted(fn) }

    override fun isGranted(projectId: Int, fn: Class<out ProjectFunction>): Boolean =
        authorisations.isGranted(projectId, fn) || groups.any { it.isGranted(projectId, fn) }

    override fun getName(): String = account.name

}