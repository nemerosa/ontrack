package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogCommit;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogCommits;

import java.util.Collections;
import java.util.List;

@Data
public class SVNChangeLogRevisions implements SCMChangeLogCommits {

    private final List<SVNChangeLogRevision> list;

    public static SVNChangeLogRevisions none() {
        return new SVNChangeLogRevisions(Collections.<SVNChangeLogRevision>emptyList());
    }

    @Override
    public List<? extends SCMChangeLogCommit> getCommits() {
        return list;
    }
}
