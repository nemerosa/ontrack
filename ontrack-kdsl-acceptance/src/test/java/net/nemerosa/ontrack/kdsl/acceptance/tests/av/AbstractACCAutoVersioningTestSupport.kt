package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.spec.Branch

abstract class AbstractACCAutoVersioningTestSupport : AbstractACCDSLTestSupport() {

    protected fun withAutoVersioning(
        code: () -> Unit,
    ) {
        TODO()
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