package net.nemerosa.ontrack.it

import net.nemerosa.ontrack.model.security.AuthenticatedUser
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.security.ProjectFunction
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Signature

class MockSecurityService : SecurityService {

    override fun checkAuthenticated() {}

    override fun checkGlobalFunction(fn: Class<out GlobalFunction>) {}

    override fun isGlobalFunctionGranted(fn: Class<out GlobalFunction>): Boolean = true

    override fun checkProjectFunction(projectId: Int, fn: Class<out ProjectFunction>) {}

    override fun isProjectFunctionGranted(projectId: Int, fn: Class<out ProjectFunction>): Boolean = true

    override val currentUser: AuthenticatedUser = MockAuthenticatedUser()

    override val currentSignature: Signature = Signature.Companion.of("test")

    override fun <T> runAsAdmin(supplier: () -> T): () -> T = supplier

    override fun <T> asAdmin(supplier: () -> T): T = supplier()

    override fun <T, R> runner(fn: (T) -> R): (T) -> R = fn
}