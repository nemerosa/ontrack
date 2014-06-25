package net.nemerosa.ontrack.extension.issues.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension;

/**
 * This class is used to represent an {@link net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration}
 * to a client. The proposed {@link #getId() id} is composed from the
 * {@link net.nemerosa.ontrack.extension.issues.IssueServiceExtension#getId() service id} and from the
 * {@link IssueServiceConfiguration#getName() configuration name}, separated by <code>//</code>.
 *
 * @see net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationIdentifier
 */
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class IssueServiceConfigurationRepresentation {

    private final String id;
    private final String name;
    private final String serviceId;

    public static IssueServiceConfigurationRepresentation of(IssueServiceExtension issueServiceExtension, IssueServiceConfiguration issueServiceConfiguration) {
        return new IssueServiceConfigurationRepresentation(
                issueServiceConfiguration.toIdentifier().format(),
                String.format("%s (%s)", issueServiceConfiguration.getName(), issueServiceExtension.getName()),
                issueServiceExtension.getId()
        );
    }

}
