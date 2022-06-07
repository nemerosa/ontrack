package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.spec.Branch
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSettings
import net.nemerosa.ontrack.kdsl.spec.extension.av.autoVersioning
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
        targetPath: String,
        targetProperty: String? = null,
        promotion: String,
    ) {
        TODO("Not yet implemented")
    }

    protected fun waitForAutoVersioningCompletion() {
        TODO()
    }

}