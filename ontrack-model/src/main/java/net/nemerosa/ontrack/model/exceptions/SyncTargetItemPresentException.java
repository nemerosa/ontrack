package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.common.BaseException;

public class SyncTargetItemPresentException extends BaseException {
    public SyncTargetItemPresentException(String type, Object itemId) {
        super("An item of type %s with ID = %s is already present and prevents the synchronisation.", type, itemId);
    }
}
