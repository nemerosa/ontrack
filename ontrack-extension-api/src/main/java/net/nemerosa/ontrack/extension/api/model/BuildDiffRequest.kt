package net.nemerosa.ontrack.extension.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.model.structure.ID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuildDiffRequest {

    /**
     * Build ID
     */
    @Wither
    private ID from;
    /**
     * Build ID
     */
    @Wither
    private ID to;

}
