package net.nemerosa.ontrack.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StandardBuildFilterData {

    private final int count;
    private final String sincePromotionLevel;
    private final String withPromotionLevel;
    private final LocalDate afterDate;
    private final LocalDate beforeDate;
    // TODO sinceValidationStamps
    // TODO withValidationStamps
    // TODO withProperty

    public static StandardBuildFilterData of(int count) {
        return new StandardBuildFilterData(count, null, null, null, null);
    }

    public StandardBuildFilterData withPromotionLevel(String withPromotionLevel) {
        return new StandardBuildFilterData(
                count,
                sincePromotionLevel,
                withPromotionLevel,
                afterDate,
                beforeDate
        );
    }

    public StandardBuildFilterData sincePromotionLevel(String sincePromotionLevel) {
        return new StandardBuildFilterData(
                count,
                sincePromotionLevel,
                withPromotionLevel,
                afterDate,
                beforeDate
        );
    }

    public StandardBuildFilterData afterDate(LocalDate afterDate) {
        return new StandardBuildFilterData(
                count,
                sincePromotionLevel,
                withPromotionLevel,
                afterDate,
                beforeDate
        );
    }

    public StandardBuildFilterData beforeDate(LocalDate beforeDate) {
        return new StandardBuildFilterData(
                count,
                sincePromotionLevel,
                withPromotionLevel,
                afterDate,
                beforeDate
        );
    }
}
