package net.nemerosa.ontrack.model.structure

/**
 * Service which, given a [Build], returns its display name.
 */
interface BuildDisplayNameService {

    /**
     * This method returns the first available display name or null if none is available.
     *
     * @param build Build for which to get the name
     */
    fun getFirstBuildDisplayName(build: Build): String?

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