package net.nemerosa.ontrack.extension.svn.support;

import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.db.TCopyEvent;
import net.nemerosa.ontrack.extension.svn.model.BuildSvnRevisionLink;
import net.nemerosa.ontrack.extension.svn.model.SVNLocation;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty;
import net.nemerosa.ontrack.extension.svn.service.SVNService;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.OptionalLong;

/**
 * Build / revision relationship based on the build name indicating a subversion tag.
 */
public abstract class AbstractTagBasedSvnRevisionLink<T> implements BuildSvnRevisionLink<T> {

    private final SVNService svnService;
    private final StructureService structureService;

    protected AbstractTagBasedSvnRevisionLink(SVNService svnService, StructureService structureService) {
        this.svnService = svnService;
        this.structureService = structureService;
    }

    @Override
    public OptionalLong getRevision(T data, Build build, SVNBranchConfigurationProperty branchConfigurationProperty) {
        // Gets the tag path
        Optional<String> oTagPath = getTagPath(data, build, branchConfigurationProperty);
        // If present
        if (oTagPath.isPresent()) {
            String tagPath = oTagPath.get();
            SVNRepository svnRepository = svnService.getRequiredSVNRepository(build.getBranch());
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

    @Override
    public String getBuildPath(T data, Build build, SVNBranchConfigurationProperty branchConfigurationProperty) {
        return getTagPath(data, build, branchConfigurationProperty).get();
    }

    @Override
    public Optional<Build> getEarliestBuild(T data, Branch branch, SVNLocation location, SVNLocation firstCopy, SVNBranchConfigurationProperty branchConfigurationProperty) {
        // Uses the copy (if available)
        if (firstCopy != null) {
            // TODO Gets the build name from the copy path
            // TODO The copy target path must comply with the tag path
            return getEarliestBuild(data, branch, firstCopy, branchConfigurationProperty);
        } else {
            return Optional.empty();
        }
    }

    protected Optional<Build> getEarliestBuild(T data, Branch branch, SVNLocation location, SVNBranchConfigurationProperty branchConfigurationProperty) {
        return extractBuildName(data, location.getPath(), branch, branchConfigurationProperty)
                .flatMap(buildName ->
                                structureService.findBuildByName(
                                        branch.getProject().getName(),
                                        branch.getName(),
                                        buildName
                                )
                );
    }

    protected Optional<String> getTagPath(T data, Build build, SVNBranchConfigurationProperty branchConfigurationProperty) {
        // Gets the tag name
        return getTagName(data, build.getName())
                .flatMap(tagName -> {
                    // Repository for the branch
                    SVNRepository svnRepository = svnService.getRequiredSVNRepository(build.getBranch());
                    // Gets the tag path
                    return svnService.getTagPathForTagName(svnRepository, branchConfigurationProperty.getBranchPath(), tagName);
                });
    }

    protected Optional<String> extractBuildName(T data, String path, Branch branch, SVNBranchConfigurationProperty branchConfigurationProperty) {
        // Repository for the branch
        SVNRepository svnRepository = svnService.getRequiredSVNRepository(branch);
        // Gets the base path
        Optional<String> oBasePath = svnService.getBasePath(svnRepository, branchConfigurationProperty.getBranchPath());
        if (!oBasePath.isPresent()) {
            return Optional.empty();
        }
        String basePath = oBasePath.get();
        // Tag base path
        String tagsBasePath = basePath + "/tags/";
        // Starting correctly
        if (StringUtils.startsWith(path, tagsBasePath)) {
            // Gets the tag part
            String token = StringUtils.substringAfter(path, tagsBasePath);
            // In case of /
            String tagName = StringUtils.substringBefore(token, "/");
            // Extracts the build name from the tag name
            return getBuildName(data, tagName);
        }
        // Not a tag
        else {
            return Optional.empty();
        }
    }

    protected abstract Optional<String> getBuildName(T data, String tagName);

    protected abstract Optional<String> getTagName(T data, String buildName);
}
