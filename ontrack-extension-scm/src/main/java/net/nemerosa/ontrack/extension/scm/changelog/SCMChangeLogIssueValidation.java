package net.nemerosa.ontrack.extension.scm.changelog;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SCMChangeLogIssueValidation {

    private final SCMChangeLogIssueValidationType type;
    private final String message;

    public static SCMChangeLogIssueValidation error(String message) {
        return new SCMChangeLogIssueValidation(SCMChangeLogIssueValidationType.ERROR, message);
    }

    public static SCMChangeLogIssueValidation warning(String message) {
        return new SCMChangeLogIssueValidation(SCMChangeLogIssueValidationType.WARNING, message);
    }

    public static SCMChangeLogIssueValidation info(String message) {
        return new SCMChangeLogIssueValidation(SCMChangeLogIssueValidationType.INFO, message);
    }

}
