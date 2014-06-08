package net.nemerosa.ontrack.extension.jenkins.client;

public interface JenkinsClient {

    JenkinsJob getJob(String jenkinsJobUrl, boolean details);

}
