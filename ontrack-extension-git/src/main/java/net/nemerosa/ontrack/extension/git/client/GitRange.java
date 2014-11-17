package net.nemerosa.ontrack.extension.git.client;

import lombok.Data;
import org.eclipse.jgit.revwalk.RevCommit;

@Data
public class GitRange {

    private final RevCommit from;
    private final RevCommit to;

}
