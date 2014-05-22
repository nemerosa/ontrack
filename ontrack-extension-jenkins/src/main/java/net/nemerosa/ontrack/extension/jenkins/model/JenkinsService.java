package net.nemerosa.ontrack.extension.jenkins.model;

import java.util.Collection;

public interface JenkinsService {

    Collection<JenkinsConfiguration> getConfigurations();

    JenkinsConfiguration newConfiguration(JenkinsConfiguration configuration);

    JenkinsConfiguration getConfiguration(String name);

    void deleteConfiguration(String name);

    void updateConfiguration(String name, JenkinsConfiguration configuration);
}
