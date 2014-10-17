package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Result of the synchronisation of branches.
 */
@Data
public class BranchTemplateSyncResults {

    private final Map<String, BranchTemplateSyncResult> sources = new LinkedHashMap<>();

    public void addBranch(String sourceName, BranchTemplateSyncResult result) {
        sources.put(sourceName, result);
    }

}
