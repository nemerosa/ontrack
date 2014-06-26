package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.structure.VersionInfo;

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

}
