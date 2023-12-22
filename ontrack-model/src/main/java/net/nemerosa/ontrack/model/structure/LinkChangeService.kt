package net.nemerosa.ontrack.model.structure

/**
 * Computing the changes between two builds in terms of dependencies.
 */
interface LinkChangeService {

    /**
     * Gets the list of dependency changes between two builds.
     *
     * First, the two builds will be ordered by creation.
     */
    fun linkChanges(from: Build, to: Build): List<LinkChange>
}