package net.nemerosa.ontrack.extension.git.client;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GitCommit {

    private final String id;
    private final GitPerson author;
    private final GitPerson committer;
    private final LocalDateTime commitTime;
    private final String fullMessage;
    private final String shortMessage;
}
