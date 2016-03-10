package net.nemerosa.ontrack.extension.jenkins.client;

import lombok.Data;

@Data
public class JenkinsJob {

    private final String name;
    private final String url;
    @Deprecated
    private final JenkinsJobResult result;
    @Deprecated
    private final JenkinsJobState state;

}
