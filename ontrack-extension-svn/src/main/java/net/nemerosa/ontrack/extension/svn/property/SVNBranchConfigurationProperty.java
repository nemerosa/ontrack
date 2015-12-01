package net.nemerosa.ontrack.extension.svn.property;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import net.nemerosa.ontrack.model.structure.ServiceConfiguration;
import org.apache.commons.lang3.StringUtils;

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
     * Revision / build link
     *
     * @see {@link net.nemerosa.ontrack.extension.svn.model.BuildSvnRevisionLink}
     */
    private final ServiceConfiguration buildRevisionLink;

    /**
     * Returns a path which has been cleaned from its trailing or leading components.
     */
    @JsonIgnore
    public String getCuredBranchPath() {
        String trim = StringUtils.trim(branchPath);
        if ("/".equals(trim)) {
            return trim;
        } else {
            return StringUtils.stripEnd(trim, "/");
        }
    }
}
