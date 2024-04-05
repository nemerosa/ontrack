package net.nemerosa.ontrack.model.structure

/**
 * Service which, given a [Build], returns its display name.
 */
interface BuildDisplayNameService {

    /**
     * Returns the first available name for this build.
     */
    fun getBuildDisplayName(build: Build): String

    /**
     * This method does not return any default value. It'll check the display name extensions
     * and will return the first eligible value.
     *
     * @param build Build for which to get the name
     * @param defaultValue If NO extension was required to provide a name, and NONE provided one, computes a default value to return
     * @return Name of the build or null if one extension was required to provide a name and did not
     */
    fun getEligibleBuildDisplayName(
        build: Build,
        defaultValue: (Build) -> String? = { it.name },
    ): String?

    /**
     * Inside a given [project], tries to find a build by using its display name or its name.
     *
     * If [onlyDisplayName] is `true`, only the display name will be used.
     */
    fun findBuildByDisplayName(
        project: Project,
        name: String,
        onlyDisplayName: Boolean,
    ): Build?

}