package net.nemerosa.ontrack.extension.issues;

import java.util.Optional;

public interface IssueServiceRegistry {

    /**
     * TODO Gets all the issue services
     */

    /**
     * Gets an issue service by its ID
     */
    IssueServiceExtension getIssueService(String id);

    /**
     * Gets an issue service by its ID. It may be present or not.
     */
    Optional<IssueServiceExtension> getOptionalIssueService(String id);

}
