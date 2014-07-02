package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.svn.client.SVNClient;
import net.nemerosa.ontrack.extension.svn.db.*;
import net.nemerosa.ontrack.extension.svn.model.*;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType;
import net.nemerosa.ontrack.extension.svn.support.SVNUtils;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.nemerosa.ontrack.extension.svn.service.SVNServiceUtils.createChangeLogRevision;

@Service
@Transactional
public class SVNServiceImpl implements SVNService {

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final IssueServiceRegistry issueServiceRegistry;
    private final SVNConfigurationService configurationService;
    private final SVNRevisionDao revisionDao;
    private final SVNIssueRevisionDao issueRevisionDao;
    private final SVNEventDao eventDao;
    private final SVNRepositoryDao repositoryDao;
    private final SVNClient svnClient;

    @Autowired
    public SVNServiceImpl(
            StructureService structureService,
            PropertyService propertyService,
            IssueServiceRegistry issueServiceRegistry,
            SVNConfigurationService configurationService,
            SVNRevisionDao revisionDao,
            SVNIssueRevisionDao issueRevisionDao,
            SVNEventDao eventDao,
            SVNRepositoryDao repositoryDao,
            SVNClient svnClient) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.issueServiceRegistry = issueServiceRegistry;
        this.configurationService = configurationService;
        this.revisionDao = revisionDao;
        this.issueRevisionDao = issueRevisionDao;
        this.eventDao = eventDao;
        this.repositoryDao = repositoryDao;
        this.svnClient = svnClient;
    }

    @Override
    public SVNRevisionInfo getRevisionInfo(SVNRepository repository, long revision) {
        TRevision t = revisionDao.get(repository.getId(), revision);
        return new SVNRevisionInfo(
                t.getRevision(),
                t.getAuthor(),
                t.getCreation(),
                t.getBranch(),
                t.getMessage(),
                repository.getRevisionBrowsingURL(t.getRevision())
        );
    }

    @Override
    public SVNRevisionPaths getRevisionPaths(SVNRepository repository, long revision) {
        // Gets the diff for the revision
        List<SVNRevisionPath> revisionPaths = svnClient.getRevisionPaths(repository, revision);
        // OK
        return new SVNRevisionPaths(
                getRevisionInfo(repository, revision),
                revisionPaths);
    }

    @Override
    public List<Long> getRevisionsForIssueKey(SVNRepository repository, String key) {
        return issueRevisionDao.findRevisionsByIssue(repository.getId(), key);
    }

    @Override
    public SVNRepository getRepository(String name) {
        SVNConfiguration configuration = configurationService.getConfiguration(name);
        return SVNRepository.of(
                repositoryDao.getOrCreateByName(configuration.getName()),
                // The configuration contained in the property's configuration is obfuscated
                // and the original one must be loaded
                configuration,
                issueServiceRegistry.getConfiguredIssueService(configuration.getIssueServiceConfigurationIdentifier())
        );
    }

    @Override
    public Optional<SVNRepositoryIssue> searchIssues(SVNRepository repository, String token) {
        ConfiguredIssueService configuredIssueService = repository.getConfiguredIssueService();
        if (configuredIssueService != null) {
            return issueRevisionDao.findIssueByKey(repository.getId(), token)
                    .map(key -> new SVNRepositoryIssue(
                                    repository,
                                    configuredIssueService.getIssue(key)
                            )
                    );
        } else {
            return Optional.empty();
        }
    }

    @Override
    public SVNIssueInfo getIssueInfo(String configurationName, String issueKey) {
        // FIXME Method net.nemerosa.ontrack.extension.svn.service.SVNServiceImpl.getIssueInfo
        // Repository
        SVNRepository repository = getRepository(configurationName);
        // Issue service
        ConfiguredIssueService configuredIssueService = repository.getConfiguredIssueService();
        if (configuredIssueService == null) {
            // No issue service configured
            return SVNIssueInfo.empty();
        }
        // Gets the details about the issue
        Issue issue = configuredIssueService.getIssue(issueKey);
        // Gets the list of revisions & their basic info (order from latest to oldest)
        List<SVNChangeLogRevision> revisions = getRevisionsForIssueKey(repository, issueKey).stream()
                .map(revision -> {
                    SVNRevisionInfo basicInfo = getRevisionInfo(repository, revision);
                    return createChangeLogRevision(
                            repository,
                            basicInfo.getPath(),
                            0,
                            revision,
                            basicInfo.getMessage(),
                            basicInfo.getAuthor(),
                            basicInfo.getDateTime()
                    );
                })
                .collect(Collectors.toList());
        // Gets the last revision (which is the first in the list)
        SVNChangeLogRevision firstRevision = revisions.get(0);
        OntrackSVNRevisionInfo revisionInfo = getOntrackRevisionInfo(repository, firstRevision.getRevision());
//      Merged revisions
//        List<Long> merges = subversionService.getMergesForRevision(repository, revisionInfo.getChangeLogRevision().getRevision());
//        List<RevisionInfo> mergedRevisionInfos = new ArrayList<>();
//        Set<String> paths = new HashSet<>();
//        for (long merge : merges) {
//            // Gets the revision info
//            RevisionInfo mergeRevisionInfo = getRevisionInfo(repository, locale, merge);
//            // If the information contains as least one build, adds it
//            if (!mergeRevisionInfo.getBuilds().isEmpty()) {
//                // Keeps only the first one for a given target path
//                String path = mergeRevisionInfo.getChangeLogRevision().getPath();
//                if (!paths.contains(path)) {
//                    mergedRevisionInfos.add(mergeRevisionInfo);
//                    paths.add(path);
//                }
//            }
//        }
//        // OK
//        return new IssueInfo(
//                repository,
//                issue,
//                subversionService.formatRevisionTime(issue.getUpdateTime()),
//                revisionInfo,
//                mergedRevisionInfos,
//                revisions
//        );
        return null;
    }

    private OntrackSVNRevisionInfo getOntrackRevisionInfo(SVNRepository repository, long revision) {

        // Gets information about the revision
        SVNRevisionInfo basicInfo = getRevisionInfo(repository, revision);
        SVNChangeLogRevision changeLogRevision = createChangeLogRevision(
                repository,
                basicInfo.getPath(),
                0,
                revision,
                basicInfo.getMessage(),
                basicInfo.getAuthor(),
                basicInfo.getDateTime()
        );

        // Gets the first copy event on this path after this revision
        SVNLocation firstCopy = getFirstCopyAfter(repository, basicInfo.toLocation());

        // Data to collect
        Collection<BuildView> buildViews = new ArrayList<>();
//        List<BranchPromotions> revisionPromotionsPerBranch = new ArrayList<>();
        // Loops over all authorised branches
        for (Project project : structureService.getProjectList()) {
            for (Branch branch : structureService.getBranchesForProject(project.getId())) {
                // Identifies a possible build given the path/revision and the first copy
                Integer buildId = lookupBuild(basicInfo.toLocation(), firstCopy, branch);
//                // Build found
//                if (buildId != null) {
//                    // Gets the build information
//                    BuildSummary buildSummary = managementService.getBuild(buildId);
//                    // Gets the promotion levels & validation stamps
//                    List<BuildPromotionLevel> promotionLevels = managementService.getBuildPromotionLevels(locale, buildId);
//                    List<BuildValidationStamp> buildValidationStamps = managementService.getBuildValidationStamps(locale, buildId);
//                    // Adds to the list
//                    buildSummaries.add(
//                            new BuildInfo(
//                                    buildSummary,
//                                    promotionLevels,
//                                    buildValidationStamps
//                            ));
//                    // Gets the promotions for this branch
//                    List<Promotion> promotions = managementService.getPromotionsForBranch(locale, branchId, buildId);
//                    if (promotions != null && !promotions.isEmpty()) {
//                        revisionPromotionsPerBranch.add(new BranchPromotions(
//                                managementService.getBranch(branchId),
//                                promotions
//                        ));
//                    }
//                }
            }
        }
//
//        // OK
//        return new RevisionInfo(
//                repository,
//                changeLogRevision,
//                buildSummaries,
//                revisionPromotionsPerBranch
//        );
        return null;
    }

    private Integer lookupBuild(SVNLocation location, SVNLocation firstCopy, Branch branch) {
        // Gets the SVN configuration for the branch
        Property<SVNBranchConfigurationProperty> configurationProperty = propertyService.getProperty(branch, SVNBranchConfigurationPropertyType.class);
        if (configurationProperty.isEmpty()) {
            return null;
        }
        // Information
        String buildPathPattern = configurationProperty.getValue().getBuildPath();
        // Revision path
        if (SVNUtils.isPathRevision(buildPathPattern)) {
            return getEarliestBuild(branch, location, buildPathPattern);
        }
//        // Tag pattern
//        else {
//            // Uses the copy (if available)
//            if (firstCopy != null) {
//                return getEarliestBuild(branchId, firstCopy, buildPathPattern);
//            } else {
//                return null;
//            }
//        }
        return null;
    }

    private Integer getEarliestBuild(Branch branch, SVNLocation location, String buildPathPattern) {
        if (SVNUtils.followsBuildPattern(location, buildPathPattern)) {
            // Gets the build name
            String buildName = SVNUtils.getBuildName(location, buildPathPattern);
//            /**
//             * If the build is defined by path@revision, the earliest build is the one
//             * that follows this revision.
//             */
//            if (SVNExplorerPathUtils.isPathRevision(pathPattern)) {
//                return managementService.findBuildAfterUsingNumericForm(branchId, buildName);
//            }
//            /**
//             * In any other case (tag or tag prefix), the build must be looked exactly
//             */
//            else {
//                return managementService.findBuildByName(branchId, buildName);
//            }
            return null;
        } else {
            return null;
        }
    }

    private SVNLocation getFirstCopyAfter(SVNRepository repository, SVNLocation location) {
        return eventDao.getFirstCopyAfter(repository.getId(), location);
    }
}
