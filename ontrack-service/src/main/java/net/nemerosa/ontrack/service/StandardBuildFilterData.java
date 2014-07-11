package net.nemerosa.ontrack.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StandardBuildFilterData {

    private final int count;
    private final String sincePromotionLevel;
    private final String withPromotionLevel;
    // TODO sinceValidationStamps
    // TODO withValidationStamps
    // TODO withProperty

    public static StandardBuildFilterData of(int count) {
        return new StandardBuildFilterData(count, null, null);
    }

    public StandardBuildFilterData withPromotionLevel(String withPromotionLevel) {
        return new StandardBuildFilterData(
                count,
                sincePromotionLevel,
                withPromotionLevel
        );
    }

    public StandardBuildFilterData sincePromotionLevel(String sincePromotionLevel) {
        return new StandardBuildFilterData(
                count,
                sincePromotionLevel,
                withPromotionLevel
        );
    }
}
