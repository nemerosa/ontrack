package net.nemerosa.ontrack.extension.git.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogCommit;
import net.nemerosa.ontrack.git.model.GitCommit;

@Data
public class GitUICommit implements SCMChangeLogCommit {

    private final GitCommit commit;
    private final String annotatedMessage;
    private final String fullAnnotatedMessage;
    private final String link;

}
