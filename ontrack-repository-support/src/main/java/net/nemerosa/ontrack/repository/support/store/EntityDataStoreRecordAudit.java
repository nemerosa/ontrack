package net.nemerosa.ontrack.repository.support.store;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.Signature;

@Data
public class EntityDataStoreRecordAudit {

    private final EntityDataStoreRecordAuditType type;
    private final Signature signature;

}
