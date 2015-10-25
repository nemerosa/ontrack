package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.db.TCopyEvent;
import net.nemerosa.ontrack.extension.svn.model.*;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty;
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationProperty;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.support.ConnectionResult;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Layer on top of the basic Subversion client and of the repositories.
 */
public interface SVNService {

    /**
     * Gets the information about a revision
     *
     * @param revision Revision to get information about
     * @return Full details about this revision
     */
    SVNRevisionInfo getRevisionInfo(SVNRepository repository, long revision);

    /**
     * Gets the list of changes for a revision
     */
    SVNRevisionPaths getRevisionPaths(SVNRepository repository, long revision);

    /**
     * Gets the list of revisions for an issue in a repository
     *
     * @param repository Repository to get the info from
     * @param key        Issue key
     * @return List of revisions associated with this issue
     */
    List<Long> getRevisionsForIssueKey(SVNRepository repository, String key);

    /**
     * Loads a {@link net.nemerosa.ontrack.extension.svn.db.SVNRepository} using its
     * configuration name.
     *
     * @param name Name of the {@link net.nemerosa.ontrack.extension.svn.model.SVNConfiguration}
     * @return Repository
     */
    SVNRepository getRepository(String name);

    /**
     * Searches a list of issues associated to this repository
     */
    Optional<SVNRepositoryIssue> searchIssues(SVNRepository repository, String token);

    SVNChangeLogRevision createChangeLogRevision(SVNRepository repository, SVNRevisionInfo basicInfo);

    void forEachConfiguredBranch(
            Predicate<SVNProjectConfigurationProperty> projectConfigurationPredicate,
            BiConsumer<Branch, SVNBranchConfigurationProperty> branchConsumer);

    /**
     * Gets the synchronisation information for a branch.
     *
     * @param branchId ID of the branch
     * @return Information
     */
    SVNSyncInfo getSyncInfo(ID branchId);

    /**
     * Gets the configuration for a branch
     */
    Optional<SVNRepository> getSVNRepository(Branch branch);

    /**
     * Gets the required configuration for a branch
     */
    SVNRepository getRequiredSVNRepository(Branch branch);

    /**
     * Gets the list of branches
     */
    List<String> getBranches(Branch branch);

    /**
     * Tests a connection
     *
     * @param configuration Configuration to test
     * @return Result of the test
     */
    ConnectionResult test(SVNConfiguration configuration);

    /**
     * Downloads the file at the given path for a branch
     */
    Optional<String> download(ID branchId, String path);

    /**
     * Gets the last copy event to this tag.
     *
     * @param id       Repository ID
     * @param tagPath  Target of the copy
     * @param maxValue Maximum revision
     * @return Copy event
     */
    TCopyEvent getLastCopyEvent(int id, String tagPath, long maxValue);


    /**
     * Given a tag name and a base branch, returns the corresponding tag path.
     *
     * @param svnRepository Repository access
     * @param branchPath    Branch or trunk path
     * @param tagName       Tag name
     * @return Path to the tag
     */
    Optional<String> getTagPathForTagName(SVNRepository svnRepository, String branchPath, String tagName);

    Optional<String> getBasePath(SVNRepository svnRepository, String branchPath);

    SVNLocation getFirstCopyAfter(SVNRepository repository, SVNLocation location);
}
