package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.AdminController;
import net.nemerosa.ontrack.job.JobStatus;
import net.nemerosa.ontrack.model.security.ApplicationManagement;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class JobStatusResourceDecorator extends AbstractResourceDecorator<JobStatus> {

    public JobStatusResourceDecorator() {
        super(JobStatus.class);
    }

    @Override
    public List<Link> links(JobStatus jobStatus, ResourceContext resourceContext) {
        return resourceContext.links()
                // Launching a job
                .link(
                        "_launch",
                        on(AdminController.class).launchJob(jobStatus.getId()),
                        resourceContext.isGlobalFunctionGranted(ApplicationManagement.class)
                                && jobStatus.canRun()
                )
                // Pausing a job
                .link(
                        "_pause",
                        on(AdminController.class).pauseJob(jobStatus.getId()),
                        resourceContext.isGlobalFunctionGranted(ApplicationManagement.class)
                                && jobStatus.canPause()
                )
                // Resuming a job
                .link(
                        "_resume",
                        on(AdminController.class).resumeJob(jobStatus.getId()),
                        resourceContext.isGlobalFunctionGranted(ApplicationManagement.class)
                                && jobStatus.canResume()
                )
                // Deleting a job
                .link(
                        "_delete",
                        on(AdminController.class).deleteJob(jobStatus.getId()),
                        resourceContext.isGlobalFunctionGranted(ApplicationManagement.class)
                                && jobStatus.canBeDeleted()
                )
                // Stopping a job
                .link(
                        "_stop",
                        on(AdminController.class).stopJob(jobStatus.getId()),
                        resourceContext.isGlobalFunctionGranted(ApplicationManagement.class)
                                && jobStatus.canBeStopped()
                )
                // OK
                .build();
    }

}
