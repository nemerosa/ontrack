package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.OptionalInt;

@Data
public class TBuildFilter {

    private final OptionalInt accountId;
    private final int branchId;
    private final String name;
    private final String type;
    private final JsonNode data;

    public boolean isShared() {
        return !accountId.isPresent();
    }

}
