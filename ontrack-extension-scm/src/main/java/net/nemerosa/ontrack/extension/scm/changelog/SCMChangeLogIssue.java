package net.nemerosa.ontrack.extension.scm.changelog;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.Issue;

@Data
public abstract class SCMChangeLogIssue {

    private final Issue issue;

}
