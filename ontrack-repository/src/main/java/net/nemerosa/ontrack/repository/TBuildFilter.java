package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class TBuildFilter {

    private final int accountId;
    private final int branchId;
    private final String name;
    private final String type;
    private final JsonNode data;

}
