package net.nemerosa.ontrack.extension.issues.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension;

/**
 * This class is used to represent an {@link net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration}
 * to a client. The proposed {@link #id id} is composed from the
 * {@link net.nemerosa.ontrack.extension.issues.IssueServiceExtension#getId() service id} and from the
 * {@link IssueServiceConfiguration#getName() configuration name}, separated by <code>//</code>.
 *
 * @see net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationIdentifier
 */
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class IssueServiceConfigurationRepresentation {

    private static final String SELF_ID = "self";

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

    /**
     * Checks if a representation ID designates the special `self` issue service.
     *
     * @param id ID to check
     */
    public static boolean isSelf(String id) {
        return SELF_ID.equals(id);
    }

    /**
     * Special representation used to designate an issue service which is linked to another configuration.
     * For example, the GitLab issue service linked to the GitLab Git configuration.
     *
     * @param name      Display name
     * @param serviceId {@link IssueServiceExtension#getId() ID} of the service
     * @see IssueServiceExtension#getId()
     */
    public static IssueServiceConfigurationRepresentation self(String name, String serviceId) {
        return new IssueServiceConfigurationRepresentation(
                SELF_ID,
                name,
                serviceId
        );
    }
}
