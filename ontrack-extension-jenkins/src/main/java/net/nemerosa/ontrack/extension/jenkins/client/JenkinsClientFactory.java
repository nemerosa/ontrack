package net.nemerosa.ontrack.extension.jenkins.client;

import net.nemerosa.ontrack.extension.jenkins.JenkinsConfiguration;

public interface JenkinsClientFactory {

    JenkinsClient getClient(JenkinsConfiguration configuration);

}
