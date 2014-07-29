package net.nemerosa.ontrack.extension.git.property;

import lombok.Data;
import net.nemerosa.ontrack.extension.git.model.GitConfiguration;

@Data
public class GitBranchConfigurationProperty {

    /**
     * Git branch
     */
    private final String branch;

    /**
     * Tag pattern
     */
    private final String tagPattern;

}
