package net.nemerosa.ontrack.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BuildIntervalFilterData {

    private final String from;
    private final String to;

    public static BuildIntervalFilterData of(String from, String to) {
        return new BuildIntervalFilterData(from, to);
    }

}
