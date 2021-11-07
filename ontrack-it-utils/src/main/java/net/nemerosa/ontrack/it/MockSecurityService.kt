package net.nemerosa.ontrack.it

import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.security.OntrackAuthenticatedUser
import net.nemerosa.ontrack.model.security.ProjectFunction
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Signature
import kotlin.reflect.KClass

class MockSecurityService : SecurityService {

    override fun checkAuthenticated() {}

    override fun checkGlobalFunction(fn: Class<out GlobalFunction>) {}

    override fun isGlobalFunctionGranted(fn: Class<out GlobalFunction>): Boolean = true

    override fun checkProjectFunction(projectId: Int, fn: Class<out ProjectFunction>) {}

    override fun isProjectFunctionGranted(projectId: Int, fn: Class<out ProjectFunction>): Boolean = true

    override val autoProjectFunctions: Set<KClass<out ProjectFunction>>
        get() = error("Not available in mock")

    override val currentAccount: OntrackAuthenticatedUser?
        get() = error("Not available in mock")

    override val currentSignature: Signature = Signature.Companion.of("test")

    override fun <T> runAsAdmin(supplier: () -> T): () -> T = supplier

    override fun <T> asAdmin(supplier: () -> T): T = supplier()

    override fun <T, R> runner(fn: (T) -> R): (T) -> R = fn
}