package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.job.JobService;
import net.nemerosa.ontrack.model.job.JobStatus;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

@RestController
@RequestMapping("/admin")
public class AdminController extends AbstractResourceController {

    private final JobService jobService;

    @Autowired
    public AdminController(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * TODO Gets the list of admin API
     */

    /**
     * Gets the list of jobs and their status
     */
    @RequestMapping(value = "jobs", method = RequestMethod.GET)
    public Resources<JobStatus> getJobs() {
        return Resources.of(
                jobService.getJobStatuses(),
                uri(MvcUriComponentsBuilder.on(getClass()).getJobs())
        );
    }

}
