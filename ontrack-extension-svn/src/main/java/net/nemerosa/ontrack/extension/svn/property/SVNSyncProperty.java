package net.nemerosa.ontrack.extension.svn.property;

import lombok.Data;

/**
 * Synchronisation property for the builds in a branch.
 */
@Data
public class SVNSyncProperty {

    /**
     * Can the builds be overridden?
     */
    private final boolean override;

    /**
     * Synchronization interval in minutes
     */
    private final int interval;

}
