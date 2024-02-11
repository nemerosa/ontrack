package net.nemerosa.ontrack.extension.scm.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.Issue;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated Will be removed in V5. Use directly the Issue class
 */
@Data
@Deprecated
public abstract class SCMChangeLogIssue {

    private final Issue issue;
    private final List<SCMChangeLogIssueValidation> validations = new ArrayList<>();

    public void addValidations(List<SCMChangeLogIssueValidation> validations) {
        this.validations.addAll(validations);
    }

}
