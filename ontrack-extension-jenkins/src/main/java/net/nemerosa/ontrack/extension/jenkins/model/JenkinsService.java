package net.nemerosa.ontrack.extension.jenkins.model;

public interface JenkinsService {

    JenkinsSettings getSettings();

    JenkinsConfiguration newConfiguration(JenkinsConfiguration configuration);

    JenkinsConfiguration getConfiguration(String name);

    void deleteConfiguration(String name);

    void updateConfiguration(String name, JenkinsConfiguration configuration);
}
