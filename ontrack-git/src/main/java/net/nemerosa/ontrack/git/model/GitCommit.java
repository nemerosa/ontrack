package net.nemerosa.ontrack.git.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GitCommit implements Comparable<GitCommit> {

    private final String id;
    private final String shortId;
    private final GitPerson author;
    private final GitPerson committer;
    private final LocalDateTime commitTime;
    private final String fullMessage;
    private final String shortMessage;

    @Override
    public int compareTo(GitCommit o) {
        return this.commitTime.compareTo(o.commitTime);
    }
}
