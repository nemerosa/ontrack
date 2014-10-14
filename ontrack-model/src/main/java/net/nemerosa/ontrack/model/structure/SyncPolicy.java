package net.nemerosa.ontrack.model.structure;

import lombok.Data;

/**
 * Behaviour to adopt when synchronising two lists.
 */
@Data
public class SyncPolicy {

    /**
     * Target already present.
     * <p>
     * <ul>
     * <li>Can be ignored</li>
     * <li>Can be replaced</li>
     * <li>Can raise an error</li>
     * </ul>
     */
    private final TargetPresentPolicy targetPresentPolicy;

    /**
     * Unknown target.
     * <p>
     * <ul>
     * <li>Can be ignored</li>
     * <li>Can be deleted</li>
     * <li>Can raise an error</li>
     * </ul>
     */
    private final UnknownTargetPolicy unknownTargetPolicy;

    public static final SyncPolicy COPY = new SyncPolicy(TargetPresentPolicy.IGNORE, UnknownTargetPolicy.IGNORE);
    public static final SyncPolicy SYNC = new SyncPolicy(TargetPresentPolicy.REPLACE, UnknownTargetPolicy.DELETE);

    public static enum TargetPresentPolicy {
        IGNORE,
        REPLACE,
        ERROR
    }

    public static enum UnknownTargetPolicy {
        IGNORE,
        DELETE,
        ERROR
    }

}
