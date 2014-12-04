package net.nemerosa.ontrack.extension.git.model;

import net.nemerosa.ontrack.model.structure.Project;

import java.util.Optional;

/**
 * Extracting the Git configuration from a project.
 */
public interface GitConfigurator {

    Optional<GitConfiguration> getConfiguration(Project project);

}
