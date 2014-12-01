package net.nemerosa.ontrack.extension.git.model;

import net.nemerosa.ontrack.git.GitRepository;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Project;

/**
 * A <code>GitConfigurator</code> is a component that can adjust a
 * {@link net.nemerosa.ontrack.extension.git.model.GitConfiguration} according to the properties
 * found on a branch.
 */
public interface GitConfigurator {

    @Deprecated
    GitConfiguration configure(GitConfiguration configuration, Branch branch);

    GitRepository configureRepository(GitRepository repository, Project project);
}
