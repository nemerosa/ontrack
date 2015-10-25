package net.nemerosa.ontrack.extension.svn.model;


import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Build;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Function;

/**
 * Defines the way to link builds to Svn revision, in order to manage the change logs, the Svn searches
 * and synchronisations.
 *
 * @param <T> Type of configuration data
 */
public interface BuildSvnRevisionLink<T> {

    // Meta information

    /**
     * ID of the link
     */
    String getId();

    /**
     * Display name for the link
     */
    String getName();

    /**
     * Clones the configuration.
     */
    T clone(T data, Function<String, String> replacementFunction);

    // Configuration

    /**
     * Parses the configuration from a JSON node
     */
    T parseData(JsonNode node);

    /**
     * Formats the configuration data as JSON
     */
    JsonNode toJson(T data);

    /**
     * Creates a form for the edition of the link configuration.
     */
    Form getForm();

    // TODO SVN integration
    // TODO The tag path has to be computed from the branch path

    /**
     * Tests if the name of a build for a branch does comply with the build link.
     *
     * @param data Link configuration
     * @param name Name of the build to test
     * @return <code>true</code> if the build name is OK
     */
    boolean isValidBuildName(T data, String name);

    /**
     * Gets the revision attached to a build. The revision is one the SVN URL defined for the build's branch.
     *
     * @param data                        Link configuration
     * @param build                       Build to get the revision for
     * @param branchConfigurationProperty SVN branch configuration
     * @return Revision if found
     */
    OptionalLong getRevision(T data, Build build, SVNBranchConfigurationProperty branchConfigurationProperty);

    /**
     * Gets the path attached to a build. This is a relative path, can a branch or tag path, can be suffixed
     * with a revision (@1345).
     *
     * @param data                        Link configuration
     * @param build                       Build
     * @param branchConfigurationProperty SVN branch configuration
     * @return Revision if found
     */
    String getBuildPath(T data, Build build, SVNBranchConfigurationProperty branchConfigurationProperty);

    /**
     * Gets the earliest build after a given SVN location.
     */
    Optional<Build> getEarliestBuild(T data, Branch branch, SVNLocation location, SVNLocation firstCopy, SVNBranchConfigurationProperty branchConfigurationProperty);
}
