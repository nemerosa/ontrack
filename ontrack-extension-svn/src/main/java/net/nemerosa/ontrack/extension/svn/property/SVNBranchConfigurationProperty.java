package net.nemerosa.ontrack.extension.svn.property;

import lombok.Data;

/**
 * Associates a {@link net.nemerosa.ontrack.model.structure.Branch branch} with a path in a Subversion repository
 * and the means to create a tag path from a {@link net.nemerosa.ontrack.model.structure.Build build}.
 */
@Data
public class SVNBranchConfigurationProperty {

    /**
     * Path of the branch in the Subversion repository. The path is relative to the root
     * of the repository.
     */
    private final String branchPath;

    /**
     * Path of a tag in the Subversion repository using a build name. The path is computed relative to the root
     * of the repository. Several placeholders can be defined in the path definition, that will be replaced
     * at runtime:
     * <p>
     * <ul>
     * <li><code>{build}</code> - the build name</li>
     * <li><code>{build:expression}</code> - the build name, which must complies to the expression. This expression
     * is a simplified regex where * stands for any character.</li>
     * </ul>
     */
    private final String buildPath;

}
