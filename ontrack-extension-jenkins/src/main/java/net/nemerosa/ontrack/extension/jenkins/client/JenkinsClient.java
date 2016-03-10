package net.nemerosa.ontrack.extension.jenkins.client;

public interface JenkinsClient {

    JenkinsJob getJob(String job);

    JenkinsInfo getInfo();

}
