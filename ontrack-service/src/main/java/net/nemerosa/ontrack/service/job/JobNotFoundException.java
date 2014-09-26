package net.nemerosa.ontrack.service.job;

import net.nemerosa.ontrack.model.exceptions.NotFoundException;

public class JobNotFoundException extends NotFoundException {
    public JobNotFoundException(long id) {
        super("Job not found: %d", id);
    }
}
