package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.Build

/**
 * Listeners to events around build links
 */
interface BuildLinkListener {

    /**
     * A build is linked to another one
     *
     * @param from Build linked from
     * @param to   Build linked to
     */
    fun onBuildLinkAdded(from: Build, to: Build) {}

    /**
     * A build is unlinked to another one
     *
     * @param from Build linked from
     * @param to   Build linked to
     */
    fun onBuildLinkDeleted(from: Build, to: Build) {}

}