package net.nemerosa.ontrack.extension.git;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.nemerosa.ontrack.extension.api.ExtensionFeatureDescription;
import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.git.model.*;
import net.nemerosa.ontrack.extension.git.service.GitConfigurationService;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.issues.model.ExportedIssues;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogIssue;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogUUIDException;
import net.nemerosa.ontrack.extension.support.AbstractExtensionController;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.buildfilter.BuildDiff;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("extension/git")
public class GitController extends AbstractExtensionController<GitExtensionFeature> {

    private final GitService gitService;
    private final GitConfigurationService configurationService;
    private final IssueServiceRegistry issueServiceRegistry;
    private final SecurityService securityService;

    private final Cache<String, GitChangeLog> logCache;

    @Autowired
    public GitController(GitExtensionFeature feature,
                         GitService gitService,
                         GitConfigurationService configurationService,
                         IssueServiceRegistry issueServiceRegistry,
                         SecurityService securityService) {
        super(feature);
        this.gitService = gitService;
        this.configurationService = configurationService;
        this.issueServiceRegistry = issueServiceRegistry;
        this.securityService = securityService;
        // Cache
        logCache = CacheBuilder.newBuilder()
                .maximumSize(20)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build();
    }

    @Override
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Resource<ExtensionFeatureDescription> getDescription() {
        return Resource.of(
                feature.getFeatureDescription(),
                uri(MvcUriComponentsBuilder.on(getClass()).getDescription())
        )
                .with("configurations", uri(on(getClass()).getConfigurations()), securityService.isGlobalFunctionGranted(GlobalSettings.class))
                ;
    }

    /**
     * Gets the configurations
     */
    @RequestMapping(value = "configurations", method = RequestMethod.GET)
    public Resources<GitConfiguration> getConfigurations() {
        return Resources.of(
                configurationService.getConfigurations(),
                uri(on(getClass()).getConfigurations())
        )
                .with(Link.CREATE, uri(on(getClass()).getConfigurationForm()))
                ;
    }

    /**
     * Form for a configuration
     */
    @RequestMapping(value = "configurations/create", method = RequestMethod.GET)
    public Form getConfigurationForm() {
        return GitConfiguration.form(issueServiceRegistry.getAvailableIssueServiceConfigurations());
    }

    /**
     * Creating a configuration
     */
    @RequestMapping(value = "configurations/create", method = RequestMethod.POST)
    public GitConfiguration newConfiguration(@RequestBody GitConfiguration configuration) {
        return configurationService.newConfiguration(configuration);
    }

    /**
     * Gets one configuration
     */
    @RequestMapping(value = "configurations/{name}", method = RequestMethod.GET)
    public GitConfiguration getConfiguration(@PathVariable String name) {
        return configurationService.getConfiguration(name);
    }

    /**
     * Deleting one configuration
     */
    @RequestMapping(value = "configurations/{name}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Ack deleteConfiguration(@PathVariable String name) {
        configurationService.deleteConfiguration(name);
        return Ack.OK;
    }

    /**
     * Update form
     */
    @RequestMapping(value = "configurations/{name}/update", method = RequestMethod.GET)
    public Form updateConfigurationForm(@PathVariable String name) {
        return configurationService.getConfiguration(name).asForm(issueServiceRegistry.getAvailableIssueServiceConfigurations());
    }

    /**
     * Updating one configuration
     */
    @RequestMapping(value = "configurations/{name}/update", method = RequestMethod.PUT)
    public GitConfiguration updateConfiguration(@PathVariable String name, @RequestBody GitConfiguration configuration) {
        configurationService.updateConfiguration(name, configuration);
        return getConfiguration(name);
    }

    /**
     * Launches the build synchronisation for a branch.
     */
    @RequestMapping(value = "sync/{branchId}", method = RequestMethod.POST)
    public Ack launchBuildSync(@PathVariable ID branchId) {
        return gitService.launchBuildSync(branchId);
    }

    /**
     * Change log entry point
     */
    @RequestMapping(value = "changelog", method = RequestMethod.GET)
    public BuildDiff changeLog(BuildDiffRequest request) {
        GitChangeLog changeLog = gitService.changeLog(request);
        // Stores in cache
        logCache.put(changeLog.getUuid(), changeLog);
        // OK
        return changeLog;
    }

    /**
     * Change log export
     */
    @RequestMapping(value = "changelog/export/{format}", method = RequestMethod.GET)
    public ResponseEntity<String> changeLog(BuildDiffRequest request, @PathVariable String format) {
        // Gets the change log
        GitChangeLog changeLog = gitService.changeLog(request);
        // Gets the issue service
        String issueServiceConfigurationIdentifier = changeLog.getScmBranch().getIssueServiceConfigurationIdentifier();
        if (StringUtils.isBlank(issueServiceConfigurationIdentifier)) {
            return new ResponseEntity<>(
                    "The branch is not configured for issues",
                    HttpStatus.NO_CONTENT
            );
        }
        ConfiguredIssueService configuredIssueService = issueServiceRegistry.getConfiguredIssueService(issueServiceConfigurationIdentifier);
        // Gets the issue change log
        GitChangeLogIssues changeLogIssues = gitService.getChangeLogIssues(changeLog);
        // List of issues
        List<Issue> issues = changeLogIssues.getList().stream()
                .map(SCMChangeLogIssue::getIssue)
                .collect(Collectors.toList());
        // Exports the change log using the given format
        ExportedIssues exportedChangeLogIssues = configuredIssueService.getIssueServiceExtension()
                .exportIssues(
                        configuredIssueService.getIssueServiceConfiguration(),
                        issues,
                        format
                );
        // Content type
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", exportedChangeLogIssues.getFormat());
        // Body and headers
        return new ResponseEntity<>(exportedChangeLogIssues.getContent(), responseHeaders, HttpStatus.OK);
    }

    private GitChangeLog getChangeLog(String uuid) {
        GitChangeLog changeLog = logCache.getIfPresent(uuid);
        if (changeLog != null) {
            return changeLog;
        } else {
            throw new SCMChangeLogUUIDException(uuid);
        }
    }

    /**
     * Change log commits
     */
    @RequestMapping(value = "changelog/{uuid}/commits", method = RequestMethod.GET)
    public GitChangeLogCommits changeLogCommits(@PathVariable String uuid) {
        // Gets the change log
        GitChangeLog changeLog = getChangeLog(uuid);
        // Cached?
        GitChangeLogCommits commits = changeLog.getCommits();
        if (commits != null) {
            return commits;
        }
        // Loads the commits
        commits = gitService.getChangeLogCommits(changeLog);
        // Stores in cache
        logCache.put(uuid, changeLog.withCommits(commits));
        // OK
        return commits;
    }

    /**
     * Change log issues
     */
    @RequestMapping(value = "changelog/{uuid}/issues", method = RequestMethod.GET)
    public GitChangeLogIssues changeLogIssues(@PathVariable String uuid) {
        // Gets the change log
        GitChangeLog changeLog = getChangeLog(uuid);
        // Cached?
        GitChangeLogIssues issues = changeLog.getIssues();
        if (issues != null) {
            return issues;
        }
        // Loads the issues
        issues = gitService.getChangeLogIssues(changeLog);
        // Stores in cache
        logCache.put(uuid, changeLog.withIssues(issues));
        // OK
        return issues;
    }

    /**
     * Change log files
     */
    @RequestMapping(value = "changelog/{uuid}/files", method = RequestMethod.GET)
    public GitChangeLogFiles changeLogFiles(@PathVariable String uuid) {
        // Gets the change log
        GitChangeLog changeLog = getChangeLog(uuid);
        // Cached?
        GitChangeLogFiles files = changeLog.getFiles();
        if (files != null) {
            return files;
        }
        // Loads the files
        files = gitService.getChangeLogFiles(changeLog);
        // Stores in cache
        logCache.put(uuid, changeLog.withFiles(files));
        // OK
        return files;
    }

    /**
     * Issue information
     */
    @RequestMapping(value = "{branchId}/issue/{issue}", method = RequestMethod.GET)
    public Resource<OntrackGitIssueInfo> issueInfo(@PathVariable ID branchId, @PathVariable String issue) {
        return Resource.of(
                gitService.getIssueInfo(branchId, issue),
                uri(on(getClass()).issueInfo(branchId, issue))
        ).withView(Build.class);
    }

    /**
     * Commit information
     */
    @RequestMapping(value = "{branchId}/commit/{commit}", method = RequestMethod.GET)
    public Resource<OntrackGitCommitInfo> commitInfo(@PathVariable ID branchId, @PathVariable String commit) {
        return Resource.of(
                gitService.getCommitInfo(branchId, commit),
                uri(on(getClass()).commitInfo(branchId, commit))
        ).withView(Build.class);
    }
}
