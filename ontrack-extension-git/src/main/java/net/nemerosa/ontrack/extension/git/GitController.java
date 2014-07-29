package net.nemerosa.ontrack.extension.git;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.nemerosa.ontrack.extension.api.ExtensionFeatureDescription;
import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.git.model.GitChangeLog;
import net.nemerosa.ontrack.extension.git.model.GitChangeLogCommits;
import net.nemerosa.ontrack.extension.git.model.GitChangeLogIssues;
import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.service.GitConfigurationService;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogUUIDException;
import net.nemerosa.ontrack.extension.support.AbstractExtensionController;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.buildfilter.BuildDiff;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.concurrent.TimeUnit;

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
}
