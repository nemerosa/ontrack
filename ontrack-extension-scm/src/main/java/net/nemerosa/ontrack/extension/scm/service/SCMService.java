package net.nemerosa.ontrack.extension.scm.service;

import net.nemerosa.ontrack.extension.scm.model.SCMPathInfo;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Common methods for the SCM accesses
 */
public interface SCMService {

    /**
     * Downloads the file at the given path for a branch
     *
     * @deprecated Use the version with the branch name
     */
    @Deprecated
    Optional<String> download(Branch branch, String path);

    /**
     * Downloads the file at the given path for a branch
     *
     * @param project Project holding the SCM configuration
     * @param scmBranch Name of the SCM branch
     * @param path Path to the file, relative to the repository
     * @return Content of the file or null if not found
     */
    @Nullable
    String download(@NotNull Project project, @NotNull String scmBranch, @NotNull String path);

    /**
     * Gets the SCM path info of a branch
     */
    Optional<SCMPathInfo> getSCMPathInfo(Branch branch);

    /**
     * Gets the name of the default SCM branch for the project
     *
     * @param project Project to get information about
     * @return Name of the default SCM branch or null if not available
     */
    @Nullable
    String getSCMDefaultBranch(@NotNull Project project);
}
