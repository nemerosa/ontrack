package net.nemerosa.ontrack.extension.git.model;

import lombok.Data;

/**
 * Request for a project Git synchronisation.
 */
@Data
public class GitSynchronisationRequest {

    /**
     * Must the repository be reset?
     */
    private final boolean reset;

    /**
     * Normal sync request
     */
    public static final GitSynchronisationRequest SYNC = new GitSynchronisationRequest(false);

}
