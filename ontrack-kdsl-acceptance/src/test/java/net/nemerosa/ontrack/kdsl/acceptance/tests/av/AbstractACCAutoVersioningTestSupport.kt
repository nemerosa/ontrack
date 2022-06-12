package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.Branch
import net.nemerosa.ontrack.kdsl.spec.Project
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSettings
import net.nemerosa.ontrack.kdsl.spec.extension.av.autoVersioning
import net.nemerosa.ontrack.kdsl.spec.settings.settings
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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

    protected fun checkMostRecentStateOfAutoVersioningAuditForSourceAndTargetBranch(
        sourceProject: Project,
        targetBranch: Branch,
        expectedMostRecentState: String?,
        expectedData: Map<String, String> = emptyMap(),
    ) {
        // Gets auto versioning entry
        val entry = ontrack.autoVersioning.audit.entries(
            size = 1,
            source = sourceProject.name,
            project = targetBranch.project.name,
            branch = targetBranch.name
        ).firstOrNull()
        // Checks the entry
        assertNotNull(entry) {
            // Checks its most recent state
            if (expectedMostRecentState != null) {
                assertEquals(expectedMostRecentState, it.mostRecentState.state)
            }
            // Checks the data
            if (expectedData.isNotEmpty()) {
                expectedData.forEach { (key, valueRegex) ->
                    val actualValue = it.mostRecentState.data[key]
                    assertNotNull(actualValue, "Data $key is expected in the audit entry") { value ->
                        assertTrue(
                            value.asText().matches(valueRegex.toRegex()),
                            "Data $key with value $value must match $valueRegex"
                        )
                    }
                }
            }
        }
    }

}