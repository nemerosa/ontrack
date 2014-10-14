package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.common.BaseException;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class SyncTargetItemUnknownException extends BaseException {
    public SyncTargetItemUnknownException(String type, Collection<?> itemIds) {
        super("Items of type %s are not present in the source and prevent the synchronisation: %s",
                type,
                StringUtils.join(itemIds, ", "));
    }
}
