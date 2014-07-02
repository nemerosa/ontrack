package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;

@Data
public class SVNRepositoryRevision {

    private final SVNRepository repository;
    private final SVNRevisionInfo revisionInfo;

}
