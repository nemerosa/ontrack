package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.job.JobService;
import net.nemerosa.ontrack.model.job.JobStatus;
import net.nemerosa.ontrack.model.support.ApplicationLogEntries;
import net.nemerosa.ontrack.model.support.ApplicationLogService;
import net.nemerosa.ontrack.model.support.Page;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public Resource<ApplicationLogEntries> getLogEntries(Page page) {
        return Resource.of(
                applicationLogService.getLogEntries(page),
                uri(on(getClass()).getLogEntries(page))
        );
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
    @RequestMapping(value = "jobs/{id:\\d+}", method = RequestMethod.POST)
    public Ack launchJob(@PathVariable long id) {
        return jobService.launchJob(id);
    }

}
