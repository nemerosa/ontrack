package net.nemerosa.ontrack.extension.jenkins.model;

import lombok.Data;
import net.nemerosa.ontrack.model.support.Configuration;

@Data
public class JenkinsConfiguration implements Configuration {

    private final String id;
    private final String url;
    private final String user;
    private final String password;

}
