package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.ACCProperties
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.Ontrack
import net.nemerosa.ontrack.kdsl.spec.extension.av.autoVersioning

object AutoVersioningUtils {

    fun waitForAutoVersioningCompletion(
        ontrack: Ontrack,
        initial: Long = 500L,
        interval: Long = 1_000L,
        timeout: Long = ACCProperties.AutoVersioning.autoVersioningCompletion,
    ) {
        waitUntil(
            task = "Auto-versioning completion",
            initial = initial,
            interval = interval,
            timeout = timeout,
        ) {
            ontrack.autoVersioning.stats.pendingOrders == 0
        }
    }
}