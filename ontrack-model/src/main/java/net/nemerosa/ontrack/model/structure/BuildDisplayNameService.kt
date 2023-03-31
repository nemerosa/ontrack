package net.nemerosa.ontrack.model.structure

/**
 * Service which, given a [Build], returns its display name.
 */
interface BuildDisplayNameService {

    fun getBuildDisplayName(build: Build): String

    /**
     * This method does not return any default value. It'll check the display name extensions
     * and will return the first eligible value.
     */
    fun getEligibleBuildDisplayName(build: Build): String?

}