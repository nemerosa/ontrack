package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.Branch
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSettings
import net.nemerosa.ontrack.kdsl.spec.extension.av.autoVersioning
import net.nemerosa.ontrack.kdsl.spec.settings.settings

abstract class AbstractACCAutoVersioningTestSupport : AbstractACCDSLTestSupport() {

    protected fun withAutoVersioning(
        enabled: Boolean = true,
        code: () -> Unit,
    ) {
        val old = ontrack.settings.autoVersioning.get()
        try {
            ontrack.settings.autoVersioning.set(
                AutoVersioningSettings(
                    enabled = enabled,
                )
            )
            code()
        } finally {
            ontrack.settings.autoVersioning.set(old)
        }
    }

    protected fun branchWithPromotion(
        name: String = "main",
        promotion: String,
        code: Branch.() -> Unit = {},
    ): Branch = project {
        branch(name = name) {
            promotion(name = promotion)
            code()
            this
        }
    }

    protected fun waitForAutoVersioningCompletion() {
        waitUntil(initial = 1_000L) {
            ontrack.autoVersioning.stats.pendingOrders == 0
        }
    }

}