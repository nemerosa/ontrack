package net.nemerosa.ontrack.extension.general;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class AutoPromotionProperty {

    /**
     * List of needed validation stamps
     */
    private final List<String> validationStamps;

}
