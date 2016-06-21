package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.model.structure.ID;

public class BuildNotFoundException extends NotFoundException {

    public BuildNotFoundException(ID id) {
        super("Build ID not found: %s", id);
    }

    public BuildNotFoundException(String project, String branch, String build) {
        super("Build not found: %s/%s/%s", project, branch, build);
    }

    public BuildNotFoundException(String project, String build) {
        super("Build not found: %s/%s", project, build);
    }
}
