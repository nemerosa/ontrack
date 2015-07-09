package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.common.Document;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.scm.model.SCMIssueCommitBranchInfo;
import net.nemerosa.ontrack.extension.svn.client.SVNClient;
import net.nemerosa.ontrack.extension.svn.db.*;
import net.nemerosa.ontrack.extension.svn.model.*;
import net.nemerosa.ontrack.extension.svn.property.*;
import net.nemerosa.ontrack.extension.svn.support.SVNUtils;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.ConnectionResult;
import net.nemerosa.ontrack.tx.Transaction;
import net.nemerosa.ontrack.tx.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    private final TransactionService transactionService;

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
            SVNClient svnClient, TransactionService transactionService) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.issueServiceRegistry = issueServiceRegistry;
        this.configurationService = configurationService;
        this.revisionDao = revisionDao;
        this.issueRevisionDao = issueRevisionDao;
        this.eventDao = eventDao;
        this.repositoryDao = repositoryDao;
        this.svnClient = svnClient;
        this.transactionService = transactionService;
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

        // For each configured branch
        Map<String, BranchRevision> branchRevisions = new HashMap<>();
        forEachConfiguredBranch(
                config -> Objects.equals(configurationName, config.getConfiguration().getName()),
                (branch, branchConfig) -> {
                    String branchPath = branchConfig.getBranchPath();
                    // List of linked issues
                    Collection<String> linkedIssues = configuredIssueService.getLinkedIssues(branch.getProject(), issue).stream()
                            .map(Issue::getKey)
                            .collect(Collectors.toList());
                    // Gets the last raw revision on this branch
                    issueRevisionDao.findLastRevisionByIssuesAndBranch(
                            repository.getId(),
                            linkedIssues,
                            branchPath
                    ).ifPresent(revision -> branchRevisions.put(branchPath, new BranchRevision(branchPath, revision, false)));
                }
        );

        // Until all revisions are complete in respect of their merges...
        while (!BranchRevision.areComplete(branchRevisions.values())) {
            // Gets the incomplete revisions
            Collection<BranchRevision> incompleteRevisions = branchRevisions.values().stream()
                    .filter(br -> !br.isComplete()).collect(Collectors.toList());
            // For each of them, gets the list of revisions it was merged to
            incompleteRevisions.forEach(br -> {
                List<Long> merges = revisionDao.getMergesForRevision(repository.getId(), br.getRevision());
                // Marks the current revision as complete
                branchRevisions.put(br.getPath(), br.complete());
                // Gets the revision info for each merged revision
                List<TRevision> revisions = merges.stream().map(r -> revisionDao.get(repository.getId(), r)).collect(Collectors.toList());
                // For each revision path, compares with current stored revision
                revisions.forEach(t -> {
                    String branch = t.getBranch();
                    // Existing branch revision?
                    BranchRevision existingBranchRevision = branchRevisions.get(branch);
                    if (existingBranchRevision == null || t.getRevision() > existingBranchRevision.getRevision()) {
                        branchRevisions.put(branch, new BranchRevision(branch, t.getRevision(), true));
                    }
                });
            });

        }

        // We now have the last revision for this issue on each branch...
        List<OntrackSVNIssueRevisionInfo> issueRevisionInfos = new ArrayList<>();
        branchRevisions.values().forEach(br -> {
            // Loads the revision info
            SVNRevisionInfo basicInfo = getRevisionInfo(repository, br.getRevision());
            SVNChangeLogRevision changeLogRevision = createChangeLogRevision(repository, basicInfo);
            // Info to collect
            OntrackSVNIssueRevisionInfo issueRevisionInfo = OntrackSVNIssueRevisionInfo.of(changeLogRevision);
            // Gets the branch from the branch path
            AtomicReference<Branch> rBranch = new AtomicReference<>();
            forEachConfiguredBranch(
                    config -> Objects.equals(configurationName, config.getConfiguration().getName()),
                    (candidate, branchConfig) -> {
                        String branchPath = branchConfig.getBranchPath();
                        if (Objects.equals(br.getPath(), branchPath)) {
                            rBranch.set(candidate);
                        }
                    }
            );
            Branch branch = rBranch.get();
            if (branch != null) {
                // Collects branch info
                SCMIssueCommitBranchInfo branchInfo = SCMIssueCommitBranchInfo.of(branch);
                // Gets the first copy event on this path after this revision
                SVNLocation firstCopy = getFirstCopyAfter(repository, basicInfo.toLocation());
                // Identifies a possible build given the path/revision and the first copy
                Optional<Build> buildAfterCommit = lookupBuild(basicInfo.toLocation(), firstCopy, branch);
                if (buildAfterCommit.isPresent()) {
                    Build build = buildAfterCommit.get();
                    // Gets the build view
                    BuildView buildView = structureService.getBuildView(build);
                    // Adds it to the list
                    branchInfo = branchInfo.withBuildView(buildView);
                    // Collects the promotions for the branch
                    branchInfo = branchInfo.withBranchStatusView(
                            structureService.getEarliestPromotionsAfterBuild(build)
                    );
                }
                // OK
                issueRevisionInfo.add(branchInfo);
            }
            // OK
            issueRevisionInfos.add(issueRevisionInfo);
        });

        // Gets the list of revisions & their basic info (order from latest to oldest)
        List<SVNChangeLogRevision> revisions = getRevisionsForIssueKey(repository, issueKey).stream()
                .map(revision -> createChangeLogRevision(repository, getRevisionInfo(repository, revision)))
                .collect(Collectors.toList());

        // OK
        return new OntrackSVNIssueInfo(
                repository.getConfiguration(),
                repository.getConfiguredIssueService().getIssueServiceConfigurationRepresentation(),
                issue,
                issueRevisionInfos,
                revisions
        );

    }

    private SVNChangeLogRevision createChangeLogRevision(SVNRepository repository, SVNRevisionInfo basicInfo) {
        return SVNServiceUtils.createChangeLogRevision(
                repository,
                basicInfo.getPath(),
                0,
                basicInfo.getRevision(),
                basicInfo.getMessage(),
                basicInfo.getAuthor(),
                basicInfo.getDateTime()
        );
    }

    public void forEachConfiguredBranch(
            Predicate<SVNProjectConfigurationProperty> projectConfigurationPredicate,
            BiConsumer<Branch, SVNBranchConfigurationProperty> branchConsumer) {
        // Loops over all authorised branches
        for (Project project : structureService.getProjectList()) {
            // Filter on SVN configuration: must be present and equal to the one the revision info is looked into
            Property<SVNProjectConfigurationProperty> projectSvnConfig = propertyService.getProperty(project, SVNProjectConfigurationPropertyType.class);
            if (!projectSvnConfig.isEmpty() && projectConfigurationPredicate.test(projectSvnConfig.getValue())) {
                structureService.getBranchesForProject(project.getId()).stream()
                        .filter(branch -> propertyService.hasProperty(branch, SVNBranchConfigurationPropertyType.class))
                        .forEach(branch -> {
                            // Branch configuration
                            SVNBranchConfigurationProperty branchConfiguration = propertyService.getProperty(branch, SVNBranchConfigurationPropertyType.class).getValue();
                            // OK
                            branchConsumer.accept(branch, branchConfiguration);
                        });
            }
        }
    }

    @Override
    public OntrackSVNRevisionInfo getOntrackRevisionInfo(SVNRepository repository, long revision) {

        // Gets information about the revision
        SVNRevisionInfo basicInfo = getRevisionInfo(repository, revision);
        SVNChangeLogRevision changeLogRevision = createChangeLogRevision(
                repository,
                basicInfo);

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

    @Override
    public ConnectionResult test(SVNConfiguration configuration) {
        //noinspection unused
        try (Transaction tx = transactionService.start()) {
            // Creates a repository
            SVNRepository repository = SVNRepository.of(
                    0,
                    configuration,
                    null
            );
            // Connection to the root
            boolean ok = svnClient.exists(
                    repository,
                    SVNUtils.toURL(configuration.getUrl()),
                    SVNRevision.HEAD
            );
            // OK
            return ok ? ConnectionResult.ok() : ConnectionResult.error(configuration.getUrl() + " does not exist.");
        } catch (Exception ex) {
            return ConnectionResult.error(ex.getMessage());
        }
    }

    @Override
    public Optional<Document> download(ID branchId, String path) {
        return getSVNRepository(structureService.getBranch(branchId))
                .flatMap(repository -> download(repository, path));
    }

    protected Optional<Document> download(SVNRepository repository, String path) {
        try (Transaction ignored = transactionService.start()) {
            return svnClient.download(repository, path);
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
