package net.nemerosa.ontrack.extension.git.property;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.model.structure.ServiceConfiguration;

@Data
public class GitBranchConfigurationProperty {

    /**
     * Git branch
     */
    private final String branch;

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
