package net.nemerosa.ontrack.extension.jenkins.client;

import lombok.Data;

@Data
public class JenkinsUser {

    private final String id;
    private final String fullName;
    private final String imageUrl;

}
