package net.nemerosa.ontrack.extension.api.model;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.ID;

@Data
public class BuildDiffRequest {

    private final ID branch;
    private final ID buildFrom;
    private final ID buildTo;

}
