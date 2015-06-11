package net.nemerosa.ontrack.extension.scm.model;

import java.util.List;

/**
 * Common attributes for a list of commits (or revisions) in a change log.
 */
public interface SCMChangeLogCommits {

    /**
     * List of commits
     */
    List<? extends SCMChangeLogCommit> getCommits();

}
