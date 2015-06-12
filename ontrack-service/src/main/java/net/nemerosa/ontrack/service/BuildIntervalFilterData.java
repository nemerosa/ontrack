package net.nemerosa.ontrack.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BuildIntervalFilterData {

    private final String fromBuild;
    private final String toBuild;

    public static BuildIntervalFilterData of(String fromBuild, String toBuild) {
        return new BuildIntervalFilterData(fromBuild, toBuild);
    }

}
