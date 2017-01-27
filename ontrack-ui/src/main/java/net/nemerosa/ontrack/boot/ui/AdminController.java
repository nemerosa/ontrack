package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.job.JobScheduler;
import net.nemerosa.ontrack.job.JobStatus;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.security.ApplicationManagement;
import net.nemerosa.ontrack.model.security.EncryptionService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ApplicationLogEntry;
import net.nemerosa.ontrack.model.support.ApplicationLogEntryFilter;
import net.nemerosa.ontrack.model.support.ApplicationLogService;
import net.nemerosa.ontrack.model.support.Page;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Pagination;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/admin")
public class AdminController extends AbstractResourceController {

    private final JobScheduler jobScheduler;
    private final ApplicationLogService applicationLogService;
    private final HealthEndpoint healthEndpoint;
    private final SecurityService securityService;
    private final EncryptionService encryptionService;

    @Autowired
    public AdminController(JobScheduler jobScheduler, ApplicationLogService applicationLogService, HealthEndpoint healthEndpoint, SecurityService securityService, EncryptionService encryptionService) {
        this.jobScheduler = jobScheduler;
        this.applicationLogService = applicationLogService;
        this.healthEndpoint = healthEndpoint;
        this.securityService = securityService;
        this.encryptionService = encryptionService;
    }

    /**
     * Gets the health status
     */
    @RequestMapping(value = "status", method = RequestMethod.GET)
    public Resource<Health> getStatus() {
        return Resource.of(
                healthEndpoint.invoke(),
                uri(on(getClass()).getStatus())
        );
    }

    /**
     * Gets the list of application log entries
     */
    @GetMapping(value = "logs")
    public Resources<ApplicationLogEntry> getLogEntries(ApplicationLogEntryFilter filter, Page page) {
        // Gets the entries
        List<ApplicationLogEntry> entries = applicationLogService.getLogEntries(
                filter,
                page
        );
        // Builds the resources
        Resources<ApplicationLogEntry> resources = Resources.of(
                entries,
                uri(on(getClass()).getLogEntries(filter, page))
        );
        // Pagination information
        int offset = page.getOffset();
        int count = page.getCount();
        int actualCount = entries.size();
        int total = applicationLogService.getLogEntriesTotal();
        Pagination pagination = Pagination.of(offset, actualCount, total);
        // Previous page
        if (offset > 0) {
            pagination = pagination.withPrev(
                    uri(on(AdminController.class).getLogEntries(
                            filter,
                            new Page(
                                    Math.max(0, offset - count),
                                    count
                            )
                    ))
            );
        }
        // Next page
        if (offset + count < total) {
            pagination = pagination.withNext(
                    uri(on(AdminController.class).getLogEntries(
                            filter,
                            new Page(
                                    offset + count,
                                    count
                            )
                    ))
            );
        }
        // OK
        return resources.withPagination(pagination);
    }

    /**
     * Deletes all application log entries
     */
    @DeleteMapping(value = "logs")
    public Ack deleteLogEntries() {
        applicationLogService.deleteLogEntries();
        return Ack.OK;
    }

    /**
     * Gets the list of jobs and their status
     */
    @RequestMapping(value = "jobs", method = RequestMethod.GET)
    public Resources<JobStatus> getJobs() {
        return Resources.of(
                jobScheduler.getJobStatuses(),
                uri(on(getClass()).getJobs())
        )
                .with(
                        "_pause",
                        uri(on(getClass()).pauseAllJobs()),
                        !jobScheduler.isPaused()
                )
                .with(
                        "_resume",
                        uri(on(getClass()).resumeAllJobs()),
                        jobScheduler.isPaused()
                )
                ;
    }

    /**
     * Launches a job
     */
    @RequestMapping(value = "jobs/{id:\\d+}", method = RequestMethod.POST)
    public Ack launchJob(@PathVariable long id) {
        securityService.checkGlobalFunction(ApplicationManagement.class);
        return jobScheduler.getJobKey(id)
                .map(key -> Ack.validate(jobScheduler.fireImmediately(key) != null))
                .orElse(Ack.NOK);
    }

    /**
     * Pauses all job executions
     */
    @PutMapping("jobs/pause")
    public Ack pauseAllJobs() {
        securityService.checkGlobalFunction(ApplicationManagement.class);
        jobScheduler.pause();
        return Ack.OK;
    }

    /**
     * Resumes all job executions
     */
    @PutMapping("jobs/resume")
    public Ack resumeAllJobs() {
        securityService.checkGlobalFunction(ApplicationManagement.class);
        jobScheduler.resume();
        return Ack.OK;
    }

    /**
     * Pauses a job
     */
    @RequestMapping(value = "jobs/{id:\\d+}/pause", method = RequestMethod.POST)
    public Ack pauseJob(@PathVariable long id) {
        securityService.checkGlobalFunction(ApplicationManagement.class);
        return jobScheduler.getJobKey(id)
                .map(key -> Ack.validate(jobScheduler.pause(key)))
                .orElse(Ack.NOK);
    }

    /**
     * Resumes a job
     */
    @RequestMapping(value = "jobs/{id:\\d+}/resume", method = RequestMethod.POST)
    public Ack resumeJob(@PathVariable long id) {
        securityService.checkGlobalFunction(ApplicationManagement.class);
        return jobScheduler.getJobKey(id)
                .map(key -> Ack.validate(jobScheduler.resume(key)))
                .orElse(Ack.NOK);
    }

    /**
     * Deleting a job
     */
    @RequestMapping(value = "jobs/{id:\\d+}", method = RequestMethod.DELETE)
    public Ack deleteJob(@PathVariable long id) {
        securityService.checkGlobalFunction(ApplicationManagement.class);
        return jobScheduler.getJobKey(id)
                .filter(key -> !jobScheduler.getJobStatus(key).get().isValid())
                .map(key -> Ack.validate(jobScheduler.unschedule(key)))
                .orElse(Ack.NOK);
    }

    /**
     * Stopping a job
     */
    @RequestMapping(value = "jobs/{id:\\d+}/stop", method = RequestMethod.DELETE)
    public Ack stopJob(@PathVariable long id) {
        securityService.checkGlobalFunction(ApplicationManagement.class);
        return jobScheduler.getJobKey(id)
                .map(key -> Ack.validate(jobScheduler.stop(key)))
                .orElse(Ack.NOK);
    }

    /**
     * Exporting the encryption key
     */
    @GetMapping("/encryption")
    public HttpEntity<String> exportEncryptionKey() {
        return ResponseEntity.ok(encryptionService.exportKey());
    }

    /**
     * Importing the encryption key
     */
    @PutMapping("/encryption")
    public HttpEntity<String> importEncryptionKey(@RequestBody String payload) {
        encryptionService.importKey(payload);
        return ResponseEntity.accepted().body(payload);
    }
}
