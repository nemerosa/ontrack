package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.Branch
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSettings
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.autoVersioning
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import net.nemerosa.ontrack.kdsl.spec.settings.settings

abstract class AbstractACCAutoVersioningTestSupport : AbstractACCDSLTestSupport() {

    protected fun withAutoVersioning(
        code: () -> Unit,
    ) {
        val old = ontrack.settings.autoVersioning.get()
        try {
            ontrack.settings.autoVersioning.set(
                AutoVersioningSettings(
                    enabled = true
                )
            )
            code()
        } finally {
            ontrack.settings.autoVersioning.set(old)
        }
    }

    protected fun branchWithPromotion(
        promotion: String,
    ): Branch = project {
        branch {
            promotion(name = promotion)
            this
        }
    }

    protected fun Branch.configuredForAutoVersioning(
        sourceProject: String,
        sourceBranch: String,
        sourcePromotion: String,
        targetPath: String,
        targetProperty: String? = null,
    ) {
        setAutoVersioningConfig(
            listOf(
                AutoVersioningSourceConfig(
                    sourceProject = sourceProject,
                    sourceBranch = sourceBranch,
                    sourcePromotion = sourcePromotion,
                    targetPath = targetPath,
                    targetProperty = targetProperty,
                )
            )
        )
    }

    protected fun waitForAutoVersioningCompletion() {
        waitUntil(initial = 1_000L) {
            ontrack.autoVersioning.stats.pendingOrders == 0
        }
    }

}