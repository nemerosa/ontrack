package net.nemerosa.ontrack.it

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.junit.jupiter.SpringExtension

class AsAdminTestExtension : BeforeEachCallback, AfterEachCallback {

    private val ns = ExtensionContext.Namespace.create(javaClass)

    override fun beforeEach(context: ExtensionContext) {
        val securityTestSupport = securityTestSupport(context)
        val admin = securityTestSupport.createAdminAccount()
        val user = securityTestSupport.createOntrackAuthenticatedUser(admin)
        val oldContext = securityTestSupport.setupSecurityContext(user)
        context.getStore(ns).put(OLD_CONTEXT, oldContext)
    }

    override fun afterEach(context: ExtensionContext) {
        val oldContext = context.getStore(ns).get(OLD_CONTEXT) as SecurityContext?
        if (oldContext != null) {
            SecurityContextHolder.setContext(oldContext)
        }
    }

    companion object {

        private const val OLD_CONTEXT = "oldContext"

        private fun securityTestSupport(context: ExtensionContext): SecurityTestSupport {
            val applicationContext = SpringExtension.getApplicationContext(context)
            return applicationContext.getBean(SecurityTestSupport::class.java)
        }
    }
}