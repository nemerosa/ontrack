package net.nemerosa.ontrack.model.support;

import lombok.Data;

/**
 * This class is used mainly for selection purposes, when we want to present a user with a list
 * of configurations.
 */
@Data
public class ConfigurationDescriptor {

    private final String id;
    private final String name;

}
