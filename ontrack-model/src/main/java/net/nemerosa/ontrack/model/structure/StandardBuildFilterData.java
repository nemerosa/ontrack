package net.nemerosa.ontrack.model.structure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.model.buildfilter.StandardFilterDataBuilder;

import java.time.LocalDate;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StandardBuildFilterData implements StandardFilterDataBuilder<StandardBuildFilterData> {

    private final int count;
    @Wither
    private final String sincePromotionLevel;
    @Wither
    private final String withPromotionLevel;
    @Wither
    private final LocalDate afterDate;
    @Wither
    private final LocalDate beforeDate;
    @Wither
    private final String sinceValidationStamp;
    @Wither
    private final String sinceValidationStampStatus;
    @Wither
    private final String withValidationStamp;
    @Wither
    private final String withValidationStampStatus;
    @Wither
    private final String withProperty;
    @Wither
    private final String withPropertyValue;
    @Wither
    private final String sinceProperty;
    @Wither
    private final String sincePropertyValue;
    @Wither
    private final String linkedFrom;
    @Wither
    private final String linkedFromPromotion;
    @Wither
    private final String linkedTo;
    @Wither
    private final String linkedToPromotion;

    public static StandardBuildFilterData of(int count) {
        return new StandardBuildFilterData(
                count,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
