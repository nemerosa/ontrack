package net.nemerosa.ontrack.model.support

import net.nemerosa.ontrack.model.structure.VersionInfo
import java.io.File

interface EnvService {
    /**
     * List of active profiles for the application
     *
     * @see net.nemerosa.ontrack.common.RunProfile
     */
    val profiles: String

    /**
     * Version of the application
     */
    val version: VersionInfo

    /**
     * Gets a working directory
     *
     * @param context General context for the working directory
     * @param name    Name of the directory into this context
     * @return A directory that exists
     */
    fun getWorkingDir(context: String, name: String): File

}
