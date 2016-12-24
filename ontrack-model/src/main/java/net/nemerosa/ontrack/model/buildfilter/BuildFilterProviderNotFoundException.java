package net.nemerosa.ontrack.model.buildfilter;

import net.nemerosa.ontrack.model.exceptions.NotFoundException;

public class BuildFilterProviderNotFoundException extends NotFoundException {

    public BuildFilterProviderNotFoundException(String type) {
        super("Build filter provider not found: %s", type);
    }
}
