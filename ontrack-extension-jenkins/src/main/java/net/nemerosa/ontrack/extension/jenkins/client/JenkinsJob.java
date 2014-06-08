package net.nemerosa.ontrack.extension.jenkins.client;

import lombok.Data;

import java.util.List;

@Data
public class JenkinsJob {

    private final String name;
    private final String url;
    private final JenkinsJobResult result;
    private final JenkinsJobState state;
    private final List<JenkinsCulprit> culprits;
    private final JenkinsBuildLink lastBuild;

}
