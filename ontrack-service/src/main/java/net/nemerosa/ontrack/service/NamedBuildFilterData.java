package net.nemerosa.ontrack.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class NamedBuildFilterData {

    @Wither
    private final int count;
    private final String fromBuild;
    @Wither
    private final String toBuild;
    @Wither
    private final String withPromotionLevel;

    public static NamedBuildFilterData of(String fromBuild) {
        return new NamedBuildFilterData(10, fromBuild, null, null);
    }
}
