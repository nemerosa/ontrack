package net.nemerosa.ontrack.extension.svn;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.api.model.FileDiffChangeLogRequest;
import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.export.ExportFormat;
import net.nemerosa.ontrack.extension.issues.export.ExportedIssues;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogIssue;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogUUIDException;
import net.nemerosa.ontrack.extension.scm.model.SCMDocumentNotFoundException;
import net.nemerosa.ontrack.extension.scm.service.SCMUtilsService;
import net.nemerosa.ontrack.extension.support.AbstractExtensionController;
import net.nemerosa.ontrack.extension.svn.model.*;
import net.nemerosa.ontrack.extension.svn.service.*;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.buildfilter.BuildDiff;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;
import net.nemerosa.ontrack.model.support.ConnectionResult;
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
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
@RequestMapping("extension/svn")
public class SVNController extends AbstractExtensionController<SVNExtensionFeature> {

    private final SVNConfigurationService svnConfigurationService;
    private final IndexationService indexationService;
    private final SVNChangeLogService changeLogService;
    private final IssueServiceRegistry issueServiceRegistry;
    private final SVNService svnService;
    private final SVNInfoService svnInfoService;
    private final SCMUtilsService scmService;
    private final SVNSyncService svnSyncService;
    private final SecurityService securityService;
    private final StructureService structureService;

    private final Cache<String, SVNChangeLog> logCache;

    @Autowired
    public SVNController(SVNExtensionFeature feature, SVNConfigurationService svnConfigurationService, IndexationService indexationService, SVNChangeLogService changeLogService, IssueServiceRegistry issueServiceRegistry, SVNService svnService, SVNInfoService svnInfoService, SCMUtilsService scmService, SVNSyncService svnSyncService, SecurityService securityService, StructureService structureService) {
        super(feature);
        this.svnConfigurationService = svnConfigurationService;
        this.indexationService = indexationService;
        this.changeLogService = changeLogService;
        this.issueServiceRegistry = issueServiceRegistry;
        this.svnService = svnService;
        this.svnInfoService = svnInfoService;
        this.scmService = scmService;
        this.svnSyncService = svnSyncService;
        this.securityService = securityService;
        this.structureService = structureService;
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
    public Resources<SVNConfiguration> getConfigurations() {
        return Resources.of(
                svnConfigurationService.getConfigurations(),
                uri(on(getClass()).getConfigurations())
        )
                .with(Link.CREATE, uri(on(getClass()).getConfigurationForm()))
                .with("_test", uri(on(getClass()).testConfiguration(null)), securityService.isGlobalFunctionGranted(GlobalSettings.class))
                ;
    }

    /**
     * Gets the configuration descriptors
     */
    @RequestMapping(value = "configurations/descriptors", method = RequestMethod.GET)
    public Resources<ConfigurationDescriptor> getConfigurationsDescriptors() {
        return Resources.of(
                svnConfigurationService.getConfigurationDescriptors(),
                uri(on(getClass()).getConfigurationsDescriptors())
        );
    }

    /**
     * Test for a configuration
     *
     * @see SVNConfigurationService#test(UserPasswordConfiguration)
     */
    @RequestMapping(value = "configurations/test", method = RequestMethod.POST)
    public ConnectionResult testConfiguration(@RequestBody SVNConfiguration configuration) {
        // Delegates to the SVNService
        return svnConfigurationService.test(configuration);
    }

    /**
     * Form for a configuration
     */
    @RequestMapping(value = "configurations/create", method = RequestMethod.GET)
    public Form getConfigurationForm() {
        return SVNConfiguration.form(issueServiceRegistry.getAvailableIssueServiceConfigurations());
    }

    /**
     * Creating a configuration
     */
    @RequestMapping(value = "configurations/create", method = RequestMethod.POST)
    public SVNConfiguration newConfiguration(@RequestBody SVNConfiguration configuration) {
        return svnConfigurationService.newConfiguration(configuration);
    }

    /**
     * Gets one configuration
     */
    @RequestMapping(value = "configurations/{name:.*}", method = RequestMethod.GET)
    public SVNConfiguration getConfiguration(@PathVariable String name) {
        return svnConfigurationService.getConfiguration(name);
    }

    /**
     * Gets the last revision for a configuration
     */
    @RequestMapping(value = "configurations/{name:.*}/indexation", method = RequestMethod.GET)
    @ResponseBody
    public LastRevisionInfo getLastRevisionInfo(@PathVariable String name) {
        return indexationService.getLastRevisionInfo(name);
    }

    /**
     * Indexation from latest
     */
    @RequestMapping(value = "configurations/{name:.*}/indexation/latest", method = RequestMethod.POST)
    @ResponseBody
    public Ack indexFromLatest(@PathVariable String name) {
        return indexationService.indexFromLatest(name);
    }

    /**
     * Full indexation
     */
    @RequestMapping(value = "configurations/{name:.*}/indexation/full", method = RequestMethod.POST)
    @ResponseBody
    public Ack full(@PathVariable String name) {
        return indexationService.reindex(name);
    }

    /**
     * Deleting one configuration
     */
    @RequestMapping(value = "configurations/{name:.*}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Ack deleteConfiguration(@PathVariable String name) {
        svnConfigurationService.deleteConfiguration(name);
        return Ack.OK;
    }

    /**
     * Update form
     */
    @RequestMapping(value = "configurations/{name:.*}/update", method = RequestMethod.GET)
    public Form updateConfigurationForm(@PathVariable String name) {
        return svnConfigurationService.getConfiguration(name).asForm(issueServiceRegistry.getAvailableIssueServiceConfigurations());
    }

    /**
     * Updating one configuration
     */
    @RequestMapping(value = "configurations/{name:.*}/update", method = RequestMethod.PUT)
    public SVNConfiguration updateConfiguration(@PathVariable String name, @RequestBody SVNConfiguration configuration) {
        svnConfigurationService.updateConfiguration(name, configuration);
        return getConfiguration(name);
    }

    /**
     * Change log export, list of formats
     */
    @RequestMapping(value = "changelog/export/{branchId}/formats", method = RequestMethod.GET)
    public Resources<ExportFormat> changeLogExportFormats(@PathVariable ID branchId) {
        return Resources.of(
                changeLogService.changeLogExportFormats(branchId),
                uri(on(SVNController.class).changeLogExportFormats(branchId))
        );
    }

    /**
     * Change log export
     */
    @RequestMapping(value = "changelog/export", method = RequestMethod.GET)
    public ResponseEntity<String> changeLog(IssueChangeLogExportRequest request) {
        // Gets the change log
        SVNChangeLog changeLog = changeLogService.changeLog(request);
        // Gets the issue service
        ConfiguredIssueService configuredIssueService = changeLog.getRepository().getConfiguredIssueService();
        if (configuredIssueService == null) {
            return new ResponseEntity<>(
                    "The branch is not configured for issues",
                    HttpStatus.NO_CONTENT
            );
        }
        // Gets the issue change log
        SVNChangeLogIssues changeLogIssues = changeLogService.getChangeLogIssues(changeLog);
        // List of issues
        List<Issue> issues = changeLogIssues.getList().stream()
                .map(SCMChangeLogIssue::getIssue)
                .collect(Collectors.toList());
        // Exports the change log using the given format
        ExportedIssues exportedChangeLogIssues = configuredIssueService.getIssueServiceExtension()
                .exportIssues(
                        configuredIssueService.getIssueServiceConfiguration(),
                        issues,
                        request
                );
        // Content type
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", exportedChangeLogIssues.getFormat());
        // Body and headers
        return new ResponseEntity<>(exportedChangeLogIssues.getContent(), responseHeaders, HttpStatus.OK);
    }

    /**
     * File diff change log
     */
    @RequestMapping(value = "changelog/diff", method = RequestMethod.GET)
    public ResponseEntity<String> diff(FileDiffChangeLogRequest request) {
        // Gets the change log
        SVNChangeLog changeLog = changeLogService.changeLog(request);
        // Gets the files
        SVNChangeLogFiles changeLogFiles = changeLogService.getChangeLogFiles(changeLog);
        // Diff export
        String diff = scmService.diff(
                changeLogFiles.getList(),
                request.getPatterns(),
                changeLogFile -> changeLogService.getDiff(changeLog.getRepository(), changeLogFile)
        );
        // Content type
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "text/plain");
        // Body and headers
        return new ResponseEntity<>(diff, responseHeaders, HttpStatus.OK);
    }

    /**
     * Change log entry point
     */
    @RequestMapping(value = "changelog", method = RequestMethod.GET)
    public BuildDiff changeLog(BuildDiffRequest request) {
        SVNChangeLog changeLog = changeLogService.changeLog(request);
        // Stores in cache
        logCache.put(changeLog.getUuid(), changeLog);
        // OK
        return changeLog;
    }

    /**
     * Change log revisions
     */
    @RequestMapping(value = "changelog/{uuid}/revisions", method = RequestMethod.GET)
    public SVNChangeLogRevisions changeLogRevisions(@PathVariable String uuid) {
        // Gets the change log
        SVNChangeLog changeLog = getChangeLog(uuid);
        // Cached?
        SVNChangeLogRevisions revisions = changeLog.getRevisions();
        if (revisions != null) {
            return revisions;
        }
        // Loads the revisions
        revisions = changeLogService.getChangeLogRevisions(changeLog);
        // Stores in cache
        logCache.put(uuid, changeLog.withRevisions(revisions));
        // OK
        return revisions;
    }

    /**
     * Change log issues
     */
    @RequestMapping(value = "changelog/{uuid}/issues", method = RequestMethod.GET)
    public SVNChangeLogIssues changeLogIssues(@PathVariable String uuid) {
        // Gets the change log
        SVNChangeLog changeLog = getChangeLog(uuid);
        // Cached?
        SVNChangeLogIssues issues = changeLog.getIssues();
        if (issues != null) {
            return issues;
        }
        // Loads the issues
        issues = changeLogService.getChangeLogIssues(changeLog);
        // Stores in cache
        logCache.put(uuid, changeLog.withIssues(issues));
        // OK
        return issues;
    }

    /**
     * Change log files
     */
    @RequestMapping(value = "changelog/{uuid}/files", method = RequestMethod.GET)
    public SVNChangeLogFiles changeLogFiles(@PathVariable String uuid) {
        // Gets the change log
        SVNChangeLog changeLog = getChangeLog(uuid);
        // Cached?
        SVNChangeLogFiles files = changeLog.getFiles();
        if (files != null) {
            return files;
        }
        // Loads the files
        files = changeLogService.getChangeLogFiles(changeLog);
        // Stores in cache
        logCache.put(uuid, changeLog.withFiles(files));
        // OK
        return files;
    }

    private SVNChangeLog getChangeLog(String uuid) {
        SVNChangeLog changeLog = logCache.getIfPresent(uuid);
        if (changeLog != null) {
            return changeLog;
        } else {
            throw new SCMChangeLogUUIDException(uuid);
        }
    }

    /**
     * Gets the summary for an issue in a repository
     */
    @RequestMapping(value = "configuration/{configuration:.*}/issue/{key}", method = RequestMethod.GET)
    public Resource<OntrackSVNIssueInfo> issueInfo(@PathVariable String configuration, @PathVariable String key) {
        return Resource.of(
                svnInfoService.getIssueInfo(configuration, key),
                uri(on(getClass()).issueInfo(configuration, key))
        ).withView(Build.class);
    }

    /**
     * Gets the summary for a revision in a repository
     */
    @RequestMapping(value = "configuration/{configuration:.*}/revision/{revision}", method = RequestMethod.GET)
    public Resource<OntrackSVNRevisionInfo> revisionInfo(@PathVariable String configuration, @PathVariable long revision) {
        return Resource.of(
                svnInfoService.getOntrackRevisionInfo(
                        svnService.getRepository(configuration),
                        revision
                ),
                uri(on(getClass()).revisionInfo(configuration, revision))
        ).withView(Build.class);
    }

    /**
     * Launches the synchronisation for a branch.
     */
    @RequestMapping(value = "sync/{branchId}", method = RequestMethod.POST)
    public SVNSyncInfoStatus launchSync(@PathVariable ID branchId) {
        return svnSyncService.launchSync(branchId);
    }

    /**
     * Download a path for a branch
     *
     * @param branchId ID to download a document from
     */
    @RequestMapping(value = "download/{branchId}")
    public ResponseEntity<String> download(@PathVariable ID branchId, String path) {
        return svnService.download(structureService.getBranch(branchId), path)
                .map(ResponseEntity::ok)
                .orElseThrow(
                        () -> new SCMDocumentNotFoundException(path)
                );
    }

}
