package net.nemerosa.ontrack.extension.jenkins;

import lombok.Data;
import net.nemerosa.ontrack.model.support.ConfigurationProperty;

@Data
public abstract class AbstractJenkinsProperty implements ConfigurationProperty<JenkinsConfiguration> {

    /**
     * Reference to the Jenkins configuration.
     */
    private final JenkinsConfiguration configuration;

}
