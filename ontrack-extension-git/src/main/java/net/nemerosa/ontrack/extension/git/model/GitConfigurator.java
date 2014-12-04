package net.nemerosa.ontrack.extension.git.model;

import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Project;

/**
 * A <code>GitConfigurator</code> is a component that can adjust a
 * {@link FormerGitConfiguration} according to the properties
 * found on a branch.
 */
public interface GitConfigurator {

    @Deprecated
    FormerGitConfiguration configure(FormerGitConfiguration configuration, Branch branch);

    FormerGitConfiguration configureProject(FormerGitConfiguration configuration, Project project);

}
