package net.nemerosa.ontrack.model.structure;

import lombok.Data;

@Data
public class BranchTemplateSyncResult {

    private final String branchName;
    private final BranchTemplateSyncType type;
    private final String message;

}
