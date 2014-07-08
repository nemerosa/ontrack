package net.nemerosa.ontrack.extension.artifactory.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArtifactoryStatus {

    private final String name;
    private final String user;
    private final LocalDateTime timestamp;

}
