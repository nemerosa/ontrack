package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.av.model.AutoVersioningConfiguredBranches
import net.nemerosa.ontrack.extension.av.tracking.AutoVersioningTrail

interface AutoVersioningDispatcher {

    fun dispatch(
        configuredBranches: AutoVersioningConfiguredBranches,
        trail: AutoVersioningTrail?
    )

}