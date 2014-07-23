package net.nemerosa.ontrack.extension.git.model;

import net.nemerosa.ontrack.model.structure.Branch;

/**
 * A <code>GitConfigurator</code> is a component that can adjust a
 * {@link net.nemerosa.ontrack.extension.git.model.GitConfiguration} according to the properties
 * found on a branch.
 */
public interface GitConfigurator {

    GitConfiguration configure(GitConfiguration configuration, Branch branch);

}
