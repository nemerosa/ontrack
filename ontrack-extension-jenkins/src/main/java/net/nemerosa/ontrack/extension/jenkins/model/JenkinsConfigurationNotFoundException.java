package net.nemerosa.ontrack.extension.jenkins.model;

import net.nemerosa.ontrack.model.exceptions.NotFoundException;

public class JenkinsConfigurationNotFoundException extends NotFoundException {
    public JenkinsConfigurationNotFoundException(String name) {
        super("Jenkins configuration not found: %s.", name);
    }
}
