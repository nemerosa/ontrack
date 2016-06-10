package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of the synchronisation of branches.
 */
@Data
public class BranchTemplateSyncResults {

    public static BranchTemplateSyncResults empty() {
        return new BranchTemplateSyncResults();
    }

    private final List<BranchTemplateSyncResult> branches = new ArrayList<>();

    public void addResult(BranchTemplateSyncResult result) {
        branches.add(result);
    }

}
