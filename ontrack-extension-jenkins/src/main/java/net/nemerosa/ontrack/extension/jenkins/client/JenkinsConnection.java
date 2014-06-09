package net.nemerosa.ontrack.extension.jenkins.client;

import lombok.Data;

/**
 * Jenkins connection parameters.
 */
@Data
public class JenkinsConnection {

    private final String url;
    private final String user;
    private final String password;

}
