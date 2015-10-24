package net.nemerosa.ontrack.extension.svn.support;

import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.db.TCopyEvent;
import net.nemerosa.ontrack.extension.svn.model.BuildSvnRevisionLink;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty;
import net.nemerosa.ontrack.extension.svn.service.SVNService;
import net.nemerosa.ontrack.model.structure.Build;

import java.util.Optional;
import java.util.OptionalLong;

/**
 * Build / revision relationship based on the build name indicating a subversion tag.
 */
public abstract class AbstractTagBasedSvnRevisionLink<T> implements BuildSvnRevisionLink<T> {

    private final SVNService svnService;

    protected AbstractTagBasedSvnRevisionLink(SVNService svnService) {
        this.svnService = svnService;
    }

    @Override
    public OptionalLong getRevision(T data, Build build, SVNBranchConfigurationProperty branchConfigurationProperty) {
        // Gets the tag name
        Optional<String> oTagName = getTagName(data, build.getName());
        if (!oTagName.isPresent()) {
            return OptionalLong.empty();
        }
        String tagName = oTagName.get();
        // Repository for the branch
        SVNRepository svnRepository = svnService.getRequiredSVNRepository(build.getBranch());
        // Gets the tag path
        Optional<String> oTagPath = svnService.getTagPathForTagName(svnRepository, branchConfigurationProperty.getBranchPath(), tagName);
        // If present
        if (oTagPath.isPresent()) {
            String tagPath = oTagPath.get();
            // Gets the copy event for this build
            TCopyEvent lastCopyEvent = svnService.getLastCopyEvent(
                    svnRepository.getId(),
                    tagPath,
                    Long.MAX_VALUE
            );
            // Gets the revision
            return lastCopyEvent != null ?
                    OptionalLong.of(lastCopyEvent.getCopyFromRevision()) :
                    OptionalLong.empty();
        } else {
            return OptionalLong.empty();
        }
    }

    protected abstract Optional<String> getTagName(T data, String buildName);
}
