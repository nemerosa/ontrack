package net.nemerosa.ontrack.extension.general;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.ValidationStamp;

import java.util.List;
import java.util.Set;

@Data
public class AutoPromotionProperty {

    /**
     * List of needed validation stamps
     *
     * FIXME Use direct reference to validation stamps
     *
     * Use `fromStorage` and co to store only the IDs, but use the complete validation stamps
     * for back to the client, in order to get a full representation of the validation stamp.
     */
    private final List<ValidationStamp> validationStamps;

}
