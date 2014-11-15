package net.nemerosa.ontrack.extension.git.property;

import lombok.Data;
import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.model.structure.ServiceConfiguration;

@Data
public class GitBranchConfigurationProperty {

    /**
     * Git branch
     */
    private final String branch;

    /**
     * Tag pattern
     *
     * @deprecated See #163
     */
    @Deprecated
    private final String tagPattern;

    /**
     * Build link
     */
    private final ServiceConfiguration buildCommitLink;

    /**
     * Build overriding policy when synchronizing
     */
    private final boolean override;

    /**
     * Interval in minutes for build/tag synchronization
     */
    private final int buildTagInterval;

}
