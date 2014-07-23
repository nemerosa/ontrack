package net.nemerosa.ontrack.model.support;

import lombok.Data;

/**
 * This class is used mainly for selection purposes, when we want to present a user with a list
 * of configurations.
 */
@Data
public class ConfigurationDescriptor {

    /**
     * Identifier for the configuration
     */
    private final String id;
    /**
     * Display name for the configuration
     */
    private final String name;

}
