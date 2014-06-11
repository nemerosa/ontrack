package net.nemerosa.ontrack.service;

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
    String getVersion();

}
