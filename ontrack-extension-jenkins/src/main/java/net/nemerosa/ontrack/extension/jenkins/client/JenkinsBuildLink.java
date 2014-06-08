package net.nemerosa.ontrack.extension.jenkins.client;

import lombok.Data;

@Data
public class JenkinsBuildLink {

    private final int number;
    private final String url;

    public String getConsoleUrl() {
        return url + "console";
    }
}
