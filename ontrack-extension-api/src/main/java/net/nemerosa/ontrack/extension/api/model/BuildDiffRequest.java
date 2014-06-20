package net.nemerosa.ontrack.extension.api.model;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.ID;

@Data
public class BuildDiffRequest {

    /**
     * Branch to get the diff on
     */
    private ID branch;
    /**
     * Build ID
     */
    private ID from;
    /**
     * Build ID
     */
    private ID to;

}
