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
     * <p/>
     * <ul>
     * <li><code>{build}</code> - the build name</li>
     * <li>TODO <code>{property:&lt;property&gt;}</code> - gets a property of the build. The property identifier
     * is the full class name of the {@link net.nemerosa.ontrack.model.structure.PropertyType PropertyType}</li>
     * </ul>
     */
    private final String buildPath;

}
