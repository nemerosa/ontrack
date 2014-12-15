package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.svn.client.SVNClient;
import net.nemerosa.ontrack.extension.svn.db.*;
import net.nemerosa.ontrack.extension.svn.model.*;
import net.nemerosa.ontrack.extension.svn.property.*;
import net.nemerosa.ontrack.extension.svn.support.SVNUtils;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
            return configuredIssueService
                    .getIssueId(token)
                    .flatMap(searchKey -> issueRevisionDao.findIssueByKey(repository.getId(), searchKey))
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
    public OntrackSVNIssueInfo getIssueInfo(String configurationName, String issueKey) {
        // Repository
        SVNRepository repository = getRepository(configurationName);
        // Issue service
        ConfiguredIssueService configuredIssueService = repository.getConfiguredIssueService();
        if (configuredIssueService == null) {
            // No issue service configured
            return OntrackSVNIssueInfo.empty(repository.getConfiguration());
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

        // Merged revisions
        List<Long> merges = revisionDao.getMergesForRevision(repository.getId(), revisionInfo.getRevisionInfo().getRevision());
        List<OntrackSVNRevisionInfo> mergedRevisionInfos = new ArrayList<>();
        Set<String> paths = new HashSet<>();
        for (long merge : merges) {
            // Gets the revision info
            OntrackSVNRevisionInfo mergeRevisionInfo = getOntrackRevisionInfo(repository, merge);
            // If the information contains as least one build, adds it
            if (!mergeRevisionInfo.getBuildViews().isEmpty()) {
                // Keeps only the first one for a given target path
                String path = mergeRevisionInfo.getRevisionInfo().getPath();
                if (!paths.contains(path)) {
                    mergedRevisionInfos.add(mergeRevisionInfo);
                    paths.add(path);
                }
            }
        }

        // OK
        return new OntrackSVNIssueInfo(
                repository.getConfiguration(),
                repository.getConfiguredIssueService().getIssueServiceConfigurationRepresentation(),
                issue,
                // FIXME #192 List of last revisions per branch
                Collections.emptyList(),
                revisionInfo,
                mergedRevisionInfos,
                revisions
        );

    }

    @Override
    public OntrackSVNRevisionInfo getOntrackRevisionInfo(SVNRepository repository, long revision) {

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
        Collection<BranchStatusView> branchStatusViews = new ArrayList<>();
        // Loops over all authorised branches
        for (Project project : structureService.getProjectList()) {
            // Filter on SVN configuration: must be present and equal to the one the revision info is looked into
            Property<SVNProjectConfigurationProperty> projectSvnConfig = propertyService.getProperty(project, SVNProjectConfigurationPropertyType.class);
            if (!projectSvnConfig.isEmpty()
                    && repository.getConfiguration().getName().equals(projectSvnConfig.getValue().getConfiguration().getName())) {
                for (Branch branch : structureService.getBranchesForProject(project.getId())) {
                    // Filter on SVN configuration: must be present
                    if (propertyService.hasProperty(branch, SVNBranchConfigurationPropertyType.class)) {
                        // Identifies a possible build given the path/revision and the first copy
                        Optional<Build> build = lookupBuild(basicInfo.toLocation(), firstCopy, branch);
                        // Build found
                        if (build.isPresent()) {
                            // Gets the build view
                            BuildView buildView = structureService.getBuildView(build.get());
                            // Adds it to the list
                            buildViews.add(buildView);
                            // Collects the promotions for the branch
                            branchStatusViews.add(
                                    structureService.getEarliestPromotionsAfterBuild(build.get())
                            );
                        }
                    }
                }
            }
        }

        // OK
        return new OntrackSVNRevisionInfo(
                repository.getConfiguration(),
                changeLogRevision,
                buildViews,
                branchStatusViews
        );

    }

    @Override
    public SVNSyncInfo getSyncInfo(ID branchId) {
        Branch branch = structureService.getBranch(branchId);
        return new SVNSyncInfo(
                branch,
                propertyService.getProperty(branch, SVNSyncPropertyType.class).getValue()
        );
    }

    @Override
    public Optional<SVNRepository> getSVNRepository(Branch branch) {
        // Gets the SVN project configuration property
        Property<SVNProjectConfigurationProperty> projectConfiguration = propertyService.getProperty(
                branch.getProject(),
                SVNProjectConfigurationPropertyType.class
        );
        if (projectConfiguration.isEmpty()) {
            return Optional.empty();
        } else {
            SVNConfiguration configuration = projectConfiguration.getValue().getConfiguration();
            return Optional.of(
                    getRepository(configuration.getName())
            );
        }
    }

    @Override
    public List<String> getBranches(Branch branch) {
        Property<SVNProjectConfigurationProperty> svnProperty = propertyService.getProperty(
                branch.getProject(),
                SVNProjectConfigurationPropertyType.class
        );
        if (svnProperty.isEmpty()) {
            return Collections.emptyList();
        } else {
            SVNRepository repository = getSVNRepository(branch).get();
            String projectPath = svnProperty.getValue().getProjectPath();
            String branchesDir;
            if (projectPath.endsWith("/trunk")) {
                branchesDir = projectPath.replace("/trunk", "/branches");
            } else {
                branchesDir = projectPath + "/branches";
            }
            return svnClient.getBranches(
                    repository,
                    SVNUtils.toURL(repository.getUrl(branchesDir))
            );
        }
    }

    private Optional<Build> lookupBuild(SVNLocation location, SVNLocation firstCopy, Branch branch) {
        // Gets the SVN configuration for the branch
        Property<SVNBranchConfigurationProperty> configurationProperty = propertyService.getProperty(branch, SVNBranchConfigurationPropertyType.class);
        if (configurationProperty.isEmpty()) {
            return Optional.empty();
        }
        // Information
        String buildPathPattern = configurationProperty.getValue().getBuildPath();
        // Revision path
        if (SVNUtils.isPathRevision(buildPathPattern)) {
            return getEarliestBuild(branch, location, buildPathPattern);
        }
        // Tag pattern
        else {
            // Uses the copy (if available)
            if (firstCopy != null) {
                return getEarliestBuild(branch, firstCopy, buildPathPattern);
            } else {
                return Optional.empty();
            }
        }
    }

    private Optional<Build> getEarliestBuild(Branch branch, SVNLocation location, String buildPathPattern) {
        if (SVNUtils.followsBuildPattern(location, buildPathPattern)) {
            // Gets the build name
            String buildName = SVNUtils.getBuildName(location, buildPathPattern);
            /**
             * If the build is defined by path@revision, the earliest build is the one
             * that follows this revision.
             */
            if (SVNUtils.isPathRevision(buildPathPattern)) {
                return structureService.findBuildAfterUsingNumericForm(branch.getId(), buildName);
            }
            /**
             * In any other case (tag or tag prefix), the build must be looked exactly
             */
            else {
                return structureService.findBuildByName(branch.getProject().getName(), branch.getName(), buildName);
            }
        } else {
            return Optional.empty();
        }
    }

    private SVNLocation getFirstCopyAfter(SVNRepository repository, SVNLocation location) {
        return eventDao.getFirstCopyAfter(repository.getId(), location);
    }
}
