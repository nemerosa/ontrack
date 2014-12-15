package net.nemerosa.ontrack.git.model;

import lombok.Data;
import org.eclipse.jgit.revwalk.RevCommit;

@Data
public class GitRange {

    private final RevCommit from;
    private final RevCommit to;

}
