package net.nemerosa.ontrack.extension.jenkins.client;

public interface JenkinsClientFactory {

    JenkinsClient getClient(JenkinsConnection connection);

}
