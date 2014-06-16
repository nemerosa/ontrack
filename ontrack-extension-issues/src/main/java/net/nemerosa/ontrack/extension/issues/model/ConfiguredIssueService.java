package net.nemerosa.ontrack.extension.issues.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension;

/**
 * Association between an {@link net.nemerosa.ontrack.extension.issues.IssueServiceExtension} and
 * one of its {@link net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration configuration}s.
 */
@Data
public class ConfiguredIssueService {

    private final IssueServiceExtension issueServiceExtension;
    private final IssueServiceConfiguration issueServiceConfiguration;

}
