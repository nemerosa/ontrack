package net.nemerosa.ontrack.extension.issues.model;

import java.time.LocalDateTime;

/**
 * Abstract definition of an issue.
 */
public interface Issue {

    String getKey();

    String getSummary();

    String getUrl();

    IssueStatus getStatus();

    LocalDateTime getUpdateTime();

}
