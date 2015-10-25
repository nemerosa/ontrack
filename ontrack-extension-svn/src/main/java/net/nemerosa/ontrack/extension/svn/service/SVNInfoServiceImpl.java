package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.scm.model.SCMIssueCommitBranchInfo;
import net.nemerosa.ontrack.extension.svn.db.SVNIssueRevisionDao;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.db.SVNRevisionDao;
import net.nemerosa.ontrack.extension.svn.db.TRevision;
import net.nemerosa.ontrack.extension.svn.model.*;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType;
import net.nemerosa.ontrack.extension.svn.support.SVNUtils;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class SVNInfoServiceImpl implements SVNInfoService {

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final SVNService svnService;
    private final BuildSvnRevisionLinkService buildSvnRevisionLinkService;
    private final SVNIssueRevisionDao issueRevisionDao;
    private final SVNRevisionDao revisionDao;

    @Autowired
    public SVNInfoServiceImpl(StructureService structureService,
                              PropertyService propertyService,
                              SVNService svnService,
                              BuildSvnRevisionLinkService buildSvnRevisionLinkService,
                              SVNIssueRevisionDao issueRevisionDao,
                              SVNRevisionDao revisionDao) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.svnService = svnService;
        this.buildSvnRevisionLinkService = buildSvnRevisionLinkService;
        this.issueRevisionDao = issueRevisionDao;
        this.revisionDao = revisionDao;
    }

    @Override
    public OntrackSVNIssueInfo getIssueInfo(String configurationName, String issueKey) {
        // Repository
        SVNRepository repository = svnService.getRepository(configurationName);
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
        svnService.forEachConfiguredBranch(
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
            SVNRevisionInfo basicInfo = svnService.getRevisionInfo(repository, br.getRevision());
            SVNChangeLogRevision changeLogRevision = svnService.createChangeLogRevision(repository, basicInfo);
            // Info to collect
            OntrackSVNIssueRevisionInfo issueRevisionInfo = OntrackSVNIssueRevisionInfo.of(changeLogRevision);
            // Gets the branch from the branch path
            AtomicReference<Branch> rBranch = new AtomicReference<>();
            svnService.forEachConfiguredBranch(
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
                SVNLocation firstCopy = svnService.getFirstCopyAfter(repository, basicInfo.toLocation());
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
        List<SVNChangeLogRevision> revisions = svnService.getRevisionsForIssueKey(repository, issueKey).stream()
                .map(revision -> svnService.createChangeLogRevision(repository, svnService.getRevisionInfo(repository, revision)))
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

    @Override
    public OntrackSVNRevisionInfo getOntrackRevisionInfo(SVNRepository repository, long revision) {
        // FIXME OntrackSVNRevisionInfo getOntrackRevisionInfo(SVNRepository repository, long revision)
        return null;
    }

    protected Optional<Build> lookupBuild(SVNLocation location, SVNLocation firstCopy, Branch branch) {
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

    protected Optional<Build> getEarliestBuild(Branch branch, SVNLocation location, String buildPathPattern) {
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
}
