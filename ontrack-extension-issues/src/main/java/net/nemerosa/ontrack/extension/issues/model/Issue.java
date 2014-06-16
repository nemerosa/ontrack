package net.nemerosa.ontrack.extension.issues.model;

import java.time.LocalDateTime;

/**
 * Abstract definition of an issue.
 */
public interface Issue {

    String getKey();

    String getSummary();

    IssueStatus getStatus();

    LocalDateTime getUpdateTime();

}
