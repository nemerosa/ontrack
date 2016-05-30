package net.nemerosa.ontrack.extension.git.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogCommit;
import net.nemerosa.ontrack.git.model.GitCommit;

import java.time.LocalDateTime;

@Data
public class GitUICommit implements SCMChangeLogCommit {

    private final GitCommit commit;
    private final String annotatedMessage;
    private final String fullAnnotatedMessage;
    private final String link;

    @Override
    public String getMessage() {
        return commit.getFullMessage();
    }

    @Override
    public String getFormattedMessage() {
        return fullAnnotatedMessage;
    }

    @Override
    public String getAuthor() {
        return commit.getCommitter().getName();
    }

    @Override
    public String getAuthorEmail() {
        return commit.getCommitter().getEmail();
    }

    @Override
    public LocalDateTime getTimestamp() {
        return commit.getCommitTime();
    }

    @Override
    public String getId() {
        return commit.getId();
    }

    @Override
    public String getShortId() {
        return commit.getShortId();
    }
}
