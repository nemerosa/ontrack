package net.nemerosa.ontrack.extension.issues.model;

import lombok.Data;
import net.nemerosa.ontrack.model.support.Selectable;

/**
 * @see IssueServiceConfigurationRepresentation
 */
@Data
public class SelectableIssueServiceConfigurationRepresentation implements Selectable {

    private final IssueServiceConfigurationRepresentation issueServiceConfigurationRepresentation;
    private final boolean selected;

    @Override
    public String getId() {
        return issueServiceConfigurationRepresentation.getId();
    }

    @Override
    public String getName() {
        return issueServiceConfigurationRepresentation.getName();
    }
}
