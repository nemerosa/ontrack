package net.nemerosa.ontrack.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;

import java.time.LocalDate;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StandardBuildFilterData {

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
    private final ValidationStampFilter sinceValidationStamp;
    // TODO withValidationStamps
    // TODO withProperty

    public static StandardBuildFilterData of(int count) {
        return new StandardBuildFilterData(count, null, null, null, null, null);
    }
}
