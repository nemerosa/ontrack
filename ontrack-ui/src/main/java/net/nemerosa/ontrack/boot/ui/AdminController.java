package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.job.JobService;
import net.nemerosa.ontrack.model.job.JobStatus;
import net.nemerosa.ontrack.model.support.ApplicationLogEntry;
import net.nemerosa.ontrack.model.support.ApplicationLogService;
import net.nemerosa.ontrack.model.support.Page;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Pagination;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/admin")
public class AdminController extends AbstractResourceController {

    private final JobService jobService;
    private final ApplicationLogService applicationLogService;

    @Autowired
    public AdminController(JobService jobService, ApplicationLogService applicationLogService) {
        this.jobService = jobService;
        this.applicationLogService = applicationLogService;
    }

    /**
     * Gets the list of application log entries
     */
    @RequestMapping(value = "logs", method = RequestMethod.GET)
    public Resources<ApplicationLogEntry> getLogEntries(Page page) {
        // Gets the entries
        List<ApplicationLogEntry> entries = applicationLogService.getLogEntries(page);
        // Builds the resources
        Resources<ApplicationLogEntry> resources = Resources.of(
                entries,
                uri(on(getClass()).getLogEntries(page))
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
     * Gets the list of jobs and their status
     */
    @RequestMapping(value = "jobs", method = RequestMethod.GET)
    public Resources<JobStatus> getJobs() {
        return Resources.of(
                jobService.getJobStatuses(),
                uri(on(getClass()).getJobs())
        );
    }

    /**
     * Launches a job
     */
    @RequestMapping(value = "jobs/{category}/{id:.*}", method = RequestMethod.POST)
    public Ack launchJob(@PathVariable String category, @PathVariable String id) {
        return jobService.launchJob(category, id);
    }

}
