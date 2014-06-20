package net.nemerosa.ontrack.extension.api.model;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.ID;

@Data
public class BuildDiffRequest {

    /**
     * Branch to get the diff on
     */
    private final ID branch;
    /**
     * Build ID
     */
    private final ID from;
    /**
     * Build ID
     */
    private final ID to;

}
