package net.nemerosa.ontrack.it

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

class NoAuthTestExtension : BeforeEachCallback, AfterEachCallback {

    private val ns = ExtensionContext.Namespace.create(javaClass)

    override fun beforeEach(context: ExtensionContext) {
        val oldContext = SecurityContextHolder.getContext()
        context.getStore(ns).put(OLD_CONTEXT, oldContext)
        SecurityContextHolder.clearContext()
    }

    override fun afterEach(context: ExtensionContext) {
        val oldContext = context.getStore(ns).get(OLD_CONTEXT) as SecurityContext?
        if (oldContext != null) {
            SecurityContextHolder.setContext(oldContext)
        }
    }

    companion object {
        private const val OLD_CONTEXT = "oldContext"
    }
}