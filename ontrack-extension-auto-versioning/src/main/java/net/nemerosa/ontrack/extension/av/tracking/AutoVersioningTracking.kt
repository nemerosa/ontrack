package net.nemerosa.ontrack.extension.av.tracking

interface AutoVersioningTracking {

    /**
     * Given an existing trail, modifies it and registers its new state.
     */
    fun withTrail(code: (trail: AutoVersioningTrail) -> AutoVersioningTrail)

    /**
     * Gets the current trail
     */
    val trail: AutoVersioningTrail?

}