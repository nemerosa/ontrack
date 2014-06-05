package net.nemerosa.ontrack.extension.jenkins;

import lombok.Data;

@Data
public abstract class AbstractJenkinsProperty {

    /**
     * Reference to the Jenkins configuration.
     */
    private final JenkinsConfiguration configuration;

}
