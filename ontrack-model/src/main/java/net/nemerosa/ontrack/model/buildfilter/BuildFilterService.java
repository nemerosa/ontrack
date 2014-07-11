package net.nemerosa.ontrack.model.buildfilter;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.structure.ID;

public interface BuildFilterService {

    BuildFilter defaultFilter();

    BuildFilters getBuildFilters(ID branchId);

    BuildFilter computeFilter(ID branchId, String type, JsonNode parameters);
}
