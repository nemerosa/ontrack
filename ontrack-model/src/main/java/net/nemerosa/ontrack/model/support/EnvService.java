package net.nemerosa.ontrack.model.support;

import net.nemerosa.ontrack.model.structure.VersionInfo;

import java.io.File;

public interface EnvService {

    /**
     * List of active profiles for the application
     *
     * @see net.nemerosa.ontrack.common.RunProfile
     */
    String getProfiles();

    /**
     * Version of the application
     */
    VersionInfo getVersion();

    /**
     * Gets a working directory
     *
     * @param context General context for the working directory
     * @param name    Name of the directory into this context
     * @return A directory that exists
     */
    File getWorkingDir(String context, String name);

}
