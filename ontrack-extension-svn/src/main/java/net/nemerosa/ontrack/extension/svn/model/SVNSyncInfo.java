package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.svn.property.SVNSyncProperty;
import net.nemerosa.ontrack.model.structure.Branch;

/**
 * Synchronisation information for a branch
 */
@Data
public class SVNSyncInfo {

    private final Branch branch;
    private final SVNSyncProperty syncProperty;

}
