package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.av.model.AutoVersioningConfiguredBranches

interface AutoVersioningDispatcher {

    fun dispatch(configuredBranches: AutoVersioningConfiguredBranches)

}