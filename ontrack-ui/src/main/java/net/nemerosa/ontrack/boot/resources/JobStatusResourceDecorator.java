package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.AdminController;
import net.nemerosa.ontrack.model.job.JobStatus;
import net.nemerosa.ontrack.model.security.ApplicationManagement;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class JobStatusResourceDecorator extends AbstractResourceDecorator<JobStatus> {

    protected JobStatusResourceDecorator() {
        super(JobStatus.class);
    }

    @Override
    public List<Link> links(JobStatus jobStatus, ResourceContext resourceContext) {
        return resourceContext.links()
                // Launching a job
                .link(
                        "_launch",
                        on(AdminController.class).launchJob(jobStatus.getDescriptor().getCategory(), jobStatus.getDescriptor().getId()),
                        ApplicationManagement.class
                )
                        // OK
                .build();
    }

}
