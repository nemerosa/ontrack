package net.nemerosa.ontrack.model.support;

import java.util.Collection;

public interface ConfigurationRepository {

    /**
     * Gets the list of items for this configuration class
     */
    <T extends Configuration> Collection<T> list(Class<T> configurationClass);

}
