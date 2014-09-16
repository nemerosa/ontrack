package net.nemerosa.ontrack.service.job;

import net.nemerosa.ontrack.model.exceptions.NotFoundException;

public class JobNotFoundException extends NotFoundException {
    public JobNotFoundException(String category, String id) {
        super("Job not found: %s @ %s", id, category);
    }
}
