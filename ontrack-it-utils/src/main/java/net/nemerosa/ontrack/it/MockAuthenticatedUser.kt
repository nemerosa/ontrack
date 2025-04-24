package net.nemerosa.ontrack.it

import net.nemerosa.ontrack.model.security.*

class MockAuthenticatedUser : AuthenticatedUser {

    override val account: Account? = null
    override val groups: List<AuthorizedGroup> = emptyList()

    override fun isGranted(fn: Class<out GlobalFunction>): Boolean = true

    override fun isGranted(
        projectId: Int,
        fn: Class<out ProjectFunction>
    ): Boolean = true

    override fun getName(): String = "test"
}