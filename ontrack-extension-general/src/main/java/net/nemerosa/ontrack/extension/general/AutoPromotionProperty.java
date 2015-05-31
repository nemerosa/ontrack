package net.nemerosa.ontrack.extension.general;

import lombok.Data;

import java.util.Set;

@Data
public class AutoPromotionProperty {

    /**
     * List of needed validation stamps
     */
    private final Set<String> validationStamps;

}
