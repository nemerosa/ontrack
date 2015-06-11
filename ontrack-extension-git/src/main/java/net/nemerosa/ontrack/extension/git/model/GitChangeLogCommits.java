package net.nemerosa.ontrack.extension.git.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogCommit;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogCommits;

import java.util.List;

@Data
public class GitChangeLogCommits implements SCMChangeLogCommits {

    private final GitUILog log;

    @Override
    public List<? extends SCMChangeLogCommit> getCommits() {
        return log.getCommits();
    }
}
