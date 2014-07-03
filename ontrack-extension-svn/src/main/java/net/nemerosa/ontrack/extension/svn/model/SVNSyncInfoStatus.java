package net.nemerosa.ontrack.extension.svn.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.model.structure.ID;

/**
 * Synchronisation status.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SVNSyncInfoStatus {

    private final ID branch;
    private final String message;

    public static SVNSyncInfoStatus of(ID branch) {
        return new SVNSyncInfoStatus(
                branch,
                null
        );
    }

    public SVNSyncInfoStatus withMessage(String message) {
        return new SVNSyncInfoStatus(
                branch,
                message
        );
    }

}
