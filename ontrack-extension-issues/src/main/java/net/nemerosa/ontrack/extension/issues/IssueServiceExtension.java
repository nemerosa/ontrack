package net.nemerosa.ontrack.extension.issues;

import net.nemerosa.ontrack.extension.api.Extension;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;

import java.util.List;

/**
 * Defines a generic service used to access issues from a ticketing system like
 * JIRA, GitHub, etc.
 */
public interface IssueServiceExtension extends Extension {

    /**
     * Gets the ID of this service. It must be unique among all the available
     * issue services. This must be mapped to the associated extension since this
     * ID can be used to identify web resources using the <code>/extension/&lt;id&gt;</code> URI.
     */
    String getId();

    /**
     * Gets the display name for this service.
     */
    String getName();

    /**
     * Returns the unfiltered list of all configurations for this issue service.
     */
    List<? extends IssueServiceConfiguration> getConfigurationList();
}
